package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import entities.game.Game;
import entities.game.GameList;
import entities.settings.ServerSettings;
import entities.settings.Settings;
import entities.user.User;
import entities.user.UserList;
import entities.user.UserRunGamesList;
import entities.user.UserRunServerList;
import helper.kryo.NetworkClassRegistrationHelper;
import message.*;
import org.apache.log4j.Logger;
import requests.ImageDownloadRequest;
import requests.DownloadRequest;
import server.upload.ImageUpload;
import server.upload.GameUpload;
import server.upload.GameUploadManager;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.*;

/**
 * {@code LANServer} manages all the communication with the {@code LANClient} and the game organisation.
 *
 * @author Levin Jeck
 * @version 2.0
 * @since 1.0
 */
public class LANServer extends Server {
    private static Logger log = Logger.getLogger(LANServer.class);
    private static final String GAMEPROPERTIES = "games";

    private volatile GameList games;
    private volatile UserList users;
    private volatile UserRunGamesList userrungames;
    private volatile UserRunServerList userrunservers;
    private File gamedirectory;
    private volatile GameUploadManager gameuploadmanager;


    /**
     * Creates and setup the server. Registering all needed listeners and classes for KryoNet. Loading the server
     * settings, binding server port, loading the gamelist, checks for a valid gamepath and if all 7zip files from the
     * games are in the gamedirectory.
     *
     * @param gamedirectory directory, where all 7zip files from the games can be found.
     */
    /**
     * Creates the {@code LANServer}.
     * <p>
     *     All listeners and classes for KryoNet get registered. The {@link ServerSettings} are loaded, the server port
     *     gets binded, the gamelist gets loaded. It is for a valid gamepath checked.
     * </p>
     *
     * @param gamedirectory directory, where all packed gamefiles are
     * @since 1.0
     */
    public LANServer(File gamedirectory){
        super();
        users = new UserList();
        gameuploadmanager = new GameUploadManager();
        userrungames = new UserRunGamesList();
        userrunservers = new UserRunServerList();
        //Register listener and classes to be send over KryoNet
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();
        //Load server settings
        Settings settings = null;
        try {
            settings = new ServerSettings();
        } catch (IOException e) {
            log.fatal("Error while creating server settings.", e);
            System.exit(-1);
        }
        //Bind server port
        try {
            bind(settings.getServerTcp(), settings.getServerUdp());
        } catch (IOException e) {
            if(e instanceof BindException) {
                log.fatal("Server address is already bound and/or server is already running. (UDP: " + settings.getServerUdp()
                    + ", TCP: " + settings.getServerTcp() + ")", e);
                System.exit(-2);
            }
        }
        //Load game list
        try {
            games = new GameList(GAMEPROPERTIES);
        } catch (IOException e) {
            log.fatal("Error while creating game list from property files.", e);
            System.exit(-3);
        }
        //Check gamedirectory to be an valid directory
        if(!gamedirectory.isDirectory()) {
            log.fatal("Entered gamepath '" + gamedirectory.getAbsolutePath() + "' isn't a directory or don't exist.");
            System.exit(-4);
        } else
            this.gamedirectory = gamedirectory;

        //Check gamedirectory for every 7zip file of all games
        games.forEach(game -> {
            File gameFile = new File(gamedirectory, game.getServerFileName());
            if(!gameFile.isFile()) {
                log.fatal("No 7zip file named '" + gameFile.getName() + "' found for '" + game + "' in '" + gamedirectory.getAbsolutePath() + "'.");
                System.exit(-5);
            }
        });
    }

    /**
     * Restarts the server with the same gamedirectory and starts it.
     *
     * @param server {@code LANServer} to restart
     * @since 1.0
     */
    private LANServer(LANServer server){
        this(server.gamedirectory);
        this.start();
    }

    @Override
    public void start() {
        super.start();
        log.info("Server started successfully.");
    }

    /**
     * Restarts the server and stops all running {@link GameUpload}.
     *
     * @return restarted {@code LanServer}
     * @since 1.0
     */
    public LANServer restart(){
        //Message all clients to stop the download
        for(Connection connection : getConnections()){
            connection.sendTCP(new DownloadStopMessage());
        }
        stop();
        close();
        //Stops all uploads
        this.gameuploadmanager.stopAll();
        return new LANServer(this);
    }

    /**
     * @return list of all available {@link Game}
     * @since 1.0
     */
    public GameList getGames(){
        return games;
    }

    /**
     * Reloads the {@link GameList} and sends it to all connected {@link User}.
     *
     * @since 1.0
     */
    public void reloadGames(){
        //Recreating the gamelist
        try {
            games = new GameList(GAMEPROPERTIES);
        } catch (IOException e) {
            log.fatal("Error while creating game list from property files.", e);
            System.exit(-6);
        }
        //Sending new gamelist to all logged in users
        sendToAllTCP(new GamelistMessage(games));
    }

    /**
     * @return list of all logged in {@link User}
     * @since 1.0
     */
    public List<User> getUsers(){
        return users.toList();
    }

    /**
     * @param user {@link User} to check the open {@link Game}
     * @return amount of open {@code Game}
     * @since 1.0
     */
    public int getOpenGamesSize(User user){
        return userrungames.get(user) == null ? 0 : userrungames.get(user).size();
    }

    /**
     * @param user {@link User} to check the open servers of the {@link Game}
     * @return amount of open {@code server}
     * @since 1.0
     */
    public int getOpenServersSize(User user){
        return userrunservers.get(user) == null ? 0 : userrunservers.get(user).size();
    }

    /**
     * @param user {@link User} to check the open {@link Game}
     * @return list of all open {@code Game} from the {@code user}
     * @since 1.0
     */
    public List<Game> getOpenGames(User user){
        return userrungames.get(user);
    }

    /**
     * @param user {@link User} to check the open servers of the {@link Game}
     * @return list of all open {@code servers} from the {@code user}
     * @since 1.0
     */
    public List<Game> getOpenServers(User user){
        return userrunservers.get(user);
    }

    /**
     * Logs the {@code user} in the {@code LANServer} and sends the {@link GameList} to the {@code user}. To all
     * connected {@link User} the new {@link UserList} is send.
     *
     * @param connection connection ID of the {@code user}
     * @param user {@link User}, who requested the log in
     * @since 1.0
     */
    private void loginPlayer(Connection connection, User user){
        //Check if user is logged in already
        if(!users.containsKey(connection.getID())){
            //Add user to the userlist
            users.put(connection.getID(), user);
            //Send user the gamelist
            connection.sendTCP(new GamelistMessage(games));
            //Send all connected users the updated userlist
            sendToAllTCP(new UserlistMessage(users));
            log.info("LOGIN: '" + user + "' logged in successfully.");
        } else {
            //Send user attempting to connect an error message
            connection.sendTCP(new ErrorMessage(ErrorMessage.userAlreadyLoggedIn));
            log.warn("Connection: " + connection.getID() + " - tried to log in, but already is logged in.");
        }
    }

    /**
     * Updates the information of the {@code user} if anything changed and sends the updated {@link UserList} to all
     * connected {@link User}.
     *
     * @param connection connection ID of the {@code user}
     * @param user {@link User}, who requested the update
     * @since 1.0
     */
    private void playerUpdate(Connection connection, User user){
        if(users.containsKey(connection.getID()) && !users.get(connection.getID()).equals(user)){
            User olduser = users.put(connection.getID(), user);
            if(olduser == null){
                log.warn("No old informations of player '" + user + "' to update.");
                return;
            }
            if(!olduser.getUsername().equals(user.getUsername()))
                log.info("PLAYER UPDATE: '" + olduser + "' changed his username to '" + user + "'.");
            if(!olduser.getOrder().equals(user.getOrder()))
                log.info("PLAYER UPDATE: '" + user + "' changed his order to: " + user.getOrder());
            if(!olduser.getIpAddress().equals(user.getIpAddress()))
                log.info("PLAYER UPDATE: '" + user + "' changed his IP-Address from '" + olduser.getIpAddress()
                        + "' to '" + user.getIpAddress() + "'.");
            if(!olduser.getGamepath().equals(user.getGamepath()))
                log.info("PLAYER UPDATE: '" + user + "' changed his gamepath from '" + olduser.getGamepath() + "' to '"
                        + user.getGamepath() + "'.");
            sendToAllTCP(new UserlistMessage(users));
        } else {
            connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
            log.warn("Connection: " + connection.getID() + " - is not logged in and tried to update.");
        }
    }

    /**
     * Registers all listeners.
     *
     * @since 1.0
     */
    private void registerListener(){
        registerLoginListener();
        registerUserupdateListener();
        registerDownloadListener();
        registerDisconnectListener();
        registerUserRunGameListener();
        registerUserRunServerListener();
        registerImageDownloadListener();
    }

    /**
     * Registers listener for new login attempts.
     *
     * @since 1.0
     */
    private void registerLoginListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof LoginMessage) {
                    LoginMessage message = (LoginMessage)object;
                    loginPlayer(connection, message.user);
                }
            }
        });
    }

    /**
     * Registers listener for {@link User} updates.
     *
     * @since 1.0
     */
    private void registerUserupdateListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserupdateMessage) {
                    UserupdateMessage message = (UserupdateMessage)object;
                    playerUpdate(connection, message.user);
                }
            }
        });
    }

    /**
     * Registers listener for download requests.
     *
     * @since 1.0
     */
    private void registerDownloadListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof DownloadRequest) {
                    DownloadRequest request = (DownloadRequest)object;
                    //Check if the user is logged in and the requested game is available, then send the game files to
                    //the user
                    if(!users.containsKey(connection.getID())){
                        log.warn("Connection: " + connection.getID() + " - is not logged in and tried to " +
                                "download a game.");
                        //Send an error message to the user
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
                    } else if (!games.contains(request.game)){
                        log.warn("'" + users.get(connection.getID()) + "' requested the game '" + request.game +
                                "', which is not available.");
                        //Send an error message to the user
                        connection.sendTCP(new ErrorMessage(ErrorMessage.gameNotOnServer + request.game));
                    } else {
                        //Send game to the user and add the upload to the upload manager
                        gameuploadmanager.add(new GameUpload(request.port, gamedirectory, request.game,
                                users.get(connection.getID())));
                    }
                }
                if(object instanceof DownloadStopMessage) {
                    DownloadStopMessage message = (DownloadStopMessage)object;
                    if(!users.containsKey(connection.getID())) {
                        log.warn("Connection: " + connection.getID() + " - is not logged in and tried to " +
                                "download a game.");
                        //Send an error message to the user
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
                        return;
                    }
                    GameUpload upload = gameuploadmanager.getUpload(message.user, message.game);
                    if(upload == null)
                        connection.sendTCP(new ErrorMessage(message.game + ErrorMessage.noGameUpload + message.user));
                    else
                        upload.stopUpload();
                }
            }
        });
    }

    /**
     * Register listener for disconnects.
     *
     * @since 1.0
     */
    private void registerDisconnectListener(){
        addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                //Remove disconnected user from the userlist
                User user = users.remove(connection.getID());
                //Check if the user was logged in and send updated userlist to all connected users
                if(user == null){
                    log.warn("Random log off request from Connection-ID: " + connection.getID());
                } else {
                    userrungames.remove(user);
                    userrunservers.remove(user);
                    log.info("LOGOFF: '" + user + "' logged off.");
                    sendToAllTCP(new UserlistMessage(users));
                    sendToAllTCP(userrungames);
                    sendToAllTCP(userrunservers);
                }
            }
        });
    }

    /**
     * Registers listener for change on {@link User} open games.
     *
     * @since 1.0
     */
    private void registerUserRunGameListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserRunGameMessage){
                    UserRunGameMessage message = (UserRunGameMessage)object;
                    if(!users.containsKey(connection.getID())){
                        log.warn("Connection: " + connection.getID() + " - is not logged in and tried to update his " +
                                "running games.");
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
                    }
                    userrungames.put(message.user, message.games);
                    StringBuilder opengames = new StringBuilder();
                    message.games.forEach(game -> opengames.append("'" + game + "' "));
                    if(message.games.isEmpty()) {
                        userrungames.remove(message.user);
                        log.info("'" + message.user + "' has closed all open games.");
                    }else
                        log.info("'" + message.user + "' has opened a game. Open games: " + opengames);
                    sendToAllTCP(userrungames);
                }
            }
        });
    }

    /**
     * Registers listener for change on {@link User} open servers.
     *
     * @since 1.0
     */
    private void registerUserRunServerListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserRunServerMessage){
                    UserRunServerMessage message = (UserRunServerMessage)object;
                    if(!users.containsKey(connection.getID())){
                        log.warn("Connection: " + connection.getID() + " - is not logged in and tried to update his " +
                                "running servers.");
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
                    }
                    userrunservers.put(message.user, message.servers);
                    StringBuilder openservers = new StringBuilder();
                    message.servers.forEach(server -> openservers.append("'" + server + "' "));
                    if(message.servers.isEmpty()) {
                        userrunservers.remove(message.user);
                        log.info("'" + message.user + "' has closed all open servers.");
                    }else
                        log.info("'" + message.user + "' has opened a server. Open servers: " + openservers);
                    sendToAllTCP(userrunservers);
                }
            }
        });
    }

    /**
     * Registers the listener for cover downloads.
     *
     * @since 1.0
     */
    private void registerImageDownloadListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof ImageDownloadRequest){
                    ImageDownloadRequest request = (ImageDownloadRequest)object;
                    //Send covers to the user
                    new ImageUpload(request.port, request.ip, new File(gamedirectory, "images"));
                }
            }
        });
    }

    /**
     * @since 1.0
     */
    public GameUploadManager getUploads(){
        return gameuploadmanager;
    }

    /**
     * @return gamedirectory, where all packed gamefiles are
     * @since 1.0
     */
    public File getGamedirectory(){
        return gamedirectory;
    }

    /**
     * Searches for the {@link Connection} of the {@code user}.
     *
     * @param user {@link User}, which {@code Connection} should be found
     * @return {@code Connection} of the {@code user} or {@code null} if no {@code Connection} found
     * @since 1.0
     */
    private Connection getConnection(User user){
        for(Connection connection : getConnections()){
            if(connection.getID() == users.getConnectionID(user))
                return connection;
        }
        return null;
    }

    /**
     * Stops the {@code upload}.
     *
     * @param upload {@link GameUpload} to be stopped
     * @since 1.0
     */
    public void stopUpload(GameUpload upload){
        getConnection(upload.getUser()).sendTCP(new DownloadStopMessage(null, upload.getGame()));
        upload.stopUpload();
        log.info("Stopped uploading '" + upload.getGame() + "' to '" + upload.getUser() + "'.");
    }

}