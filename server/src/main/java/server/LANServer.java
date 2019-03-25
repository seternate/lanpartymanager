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
import requests.DownloadRequest;
import server.upload.GameUpload;
import server.upload.GameUploadManager;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.*;

/**
 * Creates the LANServer required for lanpartymanager usage.
 */
public class LANServer extends Server {
    private static Logger log = Logger.getLogger(LANServer.class);
    private static final String GAMEPROPERTIES = "games";

    private volatile GameList games;
    private volatile UserList users;
    private volatile UserRunGamesList userrungames;
    private volatile UserRunServerList userrunservers;
    private File gamedirectory;
    private volatile GameUploadManager gameUploadManager;


    /**
     * Creates and setup the server. Registering all needed listeners and classes for KryoNet. Loading the server
     * settings, binding server port, loading the gamelist, checks for a valid gamepath and if all 7zip files from the
     * games are in the gamedirectory.
     *
     * @param gamedirectory directory, where all 7zip files from the games can be found.
     */
    public LANServer(File gamedirectory){
        super();
        users = new UserList();
        gameUploadManager = new GameUploadManager();
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
     * Recreates the server with the same gamedirectory and starts it.
     *
     * @param server that should be recreated
     */
    public LANServer(LANServer server){
        this(server.gamedirectory);
        this.start();
    }

    @Override
    public void start() {
        super.start();
        log.info("Server started successfully.");
    }

    /**
     * @return List of all games on the server.
     */
    public GameList getGames(){
        return games;
    }

    /**
     * Reloads the gamelist and sends it to all connect users.
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
     * @return List of all logged in users.
     */
    public List<User> getUsers(){
        return users.toList();
    }

    /**
     * @param user the user to determine the open games.
     * @return number of open games from the user.
     */
    public int getOpenGamesSize(User user){
        return userrungames.get(user).size();
    }

    /**
     * @param user the user to determine the open servers.
     * @return number of open servers from the user.
     */
    public int getOpenServersSize(User user){
        return userrunservers.get(user).size();
    }

    /**
     * @param user the user to get the open games from
     * @return list with all open games from the user
     */
    public List<Game> getOpenGames(User user){
        return userrungames.get(user);
    }

    /**
     * @param user the user to get the open servers from
     * @return list with all open servers from the user
     */
    public List<Game> getOpenServers(User user){
        return userrunservers.get(user);
    }

    /**
     * Logs a user in the server, saves his informations and send him the gamelist and new userlist to all connected
     * users.
     *
     * @param connection of the user who want's to get logged in.
     * @param user requested a log in.
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
     * Updates a users informations if anything changes and sends the updated userlist to all connected users.
     *
     * @param connection of the user who has send an user update.
     * @param user user who sends the user update.
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
     * Registers all needed listeners.
     */
    private void registerListener(){
        registerLoginListener();
        registerUserupdateListener();
        registerDownloadListener();
        registerDisconnectListener();
        registerUserRunGameListener();
        registerUserRunServerListener();
    }

    /**
     * Registers the listener for new login attempts.
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
     * Registers the listener for user updates.
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
     * Registers the listener for download requests.
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
                        //Get ip-address from the user
                        String ipAddress = connection.getRemoteAddressTCP().getAddress().getHostAddress();
                        //Send game to the user and add the upload to the upload manager
                        gameUploadManager.add(new GameUpload(ipAddress, request.port, gamedirectory, request.game,
                                users.get(connection.getID())));
                    }
                }
            }
        });
    }

    /**
     * Register the listener for disconnects.
     */
    private void registerDisconnectListener(){
        //TODO: opengames und servers update
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
     * Registers the listener for change on users open games.
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
                    if(message.games.isEmpty())
                        log.info("'" + message.user + "' has closed all open games.");
                    else
                        log.info("'" + message.user + "' has opened a game. Open games: " + opengames);
                    sendToAllTCP(userrungames);
                }
            }
        });
    }

    /**
     * Registers the listener for change on users open servers.
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
                    if(message.servers.isEmpty())
                        log.info("'" + message.user + "' has closed all open servers.");
                    else
                        log.info("'" + message.user + "' has opened a server. Open servers: " + openservers);
                    sendToAllTCP(userrunservers);
                }
            }
        });
    }

    /**
     * @return GameUploadManager of all running uploads.
     */
    public GameUploadManager getUploads(){
        return gameUploadManager;
    }

}