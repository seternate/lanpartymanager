package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import entities.*;
import helper.NetworkClassRegistrationHelper;
import message.*;
import org.apache.log4j.Logger;
import requests.DownloadRequest;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.*;

/**
 *
 */
public class MyServer extends Server {
    private static Logger log = Logger.getLogger(MyServer.class);
    private static final String GAMEPROPERTIES = "games";

    private GameList games;
    private UserList users;
    private File gamedirectory;


    /**
     * Creates and setup the server. Registering all needed listeners and classes for KryoNet. Loading the server
     * settings, binding server port, loading the gamelist, checks for a valid gamepath and if all 7zip files from the
     * games are in the gamedirectory.
     *
     * @param gamedirectory directory, where all 7zip files from the games can be found.
     */
    public MyServer(File gamedirectory){
        super();
        users = new UserList();
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
    public MyServer(MyServer server){
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
     * Logs a user in the server, saves his informations and send him the gamelist and new userlist to all connected
     * users.
     *
     * @param connection of the user who want's to get logged in.
     * @param user requested a log in.
     * @return true if a user could be logged in successfully, else false.
     */
    private void loginPlayer(Connection connection, User user){
        //Check if user is logged in already
        if(!users.containsKey(connection.getID())){
            //Add user to the userlist
            users.put(connection.getID(), user);
            connection.sendTCP(new GamelistMessage(games));
            sendToAllTCP(new UserlistMessage(users));
            log.info("LOGIN: '" + user + "' logged in successfully.");
        } else {
            connection.sendTCP(new ErrorMessage(ErrorMessage.userAlreadyLoggedIn));
            log.warn(user + " tried to log in, but already is logged in.");
        }
    }

    /**
     * Updates a users informations if anything changes and sends the updated userlist to all connected users.
     *
     * @param connection of the user who has send an user update.
     * @param user user who sends the user update.
     * @return true if any information of the user has changed, else false.
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
            log.warn(user + " is not logged in and tried to update.");
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
                    if(!users.containsKey(connection.getID())){
                        System.err.println("ERROR: " + users.get(connection.getID()) + " is not logged in.\n");
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
                        return;
                    }
                    if(!games.contains(request.game)){
                        System.err.println("ERROR: No game '" + request.game + "' found on the server.\n");
                        connection.sendTCP(new ErrorMessage(ErrorMessage.gameNotOnServer + request.game));
                        return;
                    }
                    String ipAddress = connection.getRemoteAddressTCP().getAddress().getHostAddress();
                    File gameFile = new File(gamedirectory , request.game.getServerFileName());
                    new GameFileSender(ipAddress, request.port, gameFile, request.game.getName(),
                            users.get(connection.getID()).getUsername());
                }
            }
        });
    }

    /**
     * Register the listener for disconnects.
     */
    private void registerDisconnectListener(){
        addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                User user = users.remove(connection.getID());
                if(user == null){
                    System.err.println("ERROR: Random Player logoff with ID '" + connection.getID() + "'.\n");
                    return;
                }
                System.out.println("LOGOFF: " + user + "\n");
                sendToAllTCP(new UserlistMessage(users));
            }
        });
    }
}