package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
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
public final class MyServer extends com.esotericsoftware.kryonet.Server {
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
        log.info("Server started.");
    }

    public GameList getGames(){
        return games;
    }

    public List<User> getUsersAsList(){
        return users.toList();
    }

    public void updateGames(){
        try {
            games = new GameList(GAMEPROPERTIES);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-13);
        }
        sendToAllTCP(new GamelistMessage(games));
    }

    private boolean loginPlayer(Connection connection, User user){
        if(!users.containsKey(connection.getID())){
            users.put(connection.getID(), user);
            System.out.println("LOGIN: " + user + " logged in.\n");
            return true;
        }
        return false;
    }

    private boolean playerUpdate(Connection connection, User user){
        if(users.containsKey(connection.getID()) && !users.get(connection.getID()).equals(user)){
            User olduser = users.put(connection.getID(), user);
            assert olduser != null;
            if(!olduser.getUsername().equals(user.getUsername()))
                System.out.println("PLAYER UPDATE: " + olduser + " changed his username to " + user + ".\n");
            if(!olduser.getOrder().equals(user.getOrder()))
                System.out.println("PLAYER UPDATE: " + user + " changed his order.\n");
            if(!olduser.getIpAddress().equals(user.getIpAddress()))
                System.out.println("PLAYER UPDATE: " + user + " changed his IP-Address to '" + user.getIpAddress() + "'.\n");
            return true;
        }
        return false;
    }

    private void registerListener(){
        registerLoginListener();
        registerUserupdateListener();
        registerDownloadListener();
        registerDisconnectListener();
    }

    private void registerLoginListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof LoginMessage) {
                    LoginMessage message = (LoginMessage)object;
                    if(!loginPlayer(connection, message.user)) {
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userAlreadyLoggedIn));
                        System.err.println("ERROR: " + message.user + " is already logged in.\n");
                        return;
                    }
                    connection.sendTCP(new GamelistMessage(games));
                    sendToAllTCP(new UserlistMessage(users));
                }
            }
        });
    }

    private void registerUserupdateListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserupdateMessage) {
                    UserupdateMessage message = (UserupdateMessage)object;
                    if(!playerUpdate(connection, message.user)) {
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
                        System.err.println("ERROR: " + message.user + " is not logged in.\n");
                        return;
                    }
                    sendToAllTCP(new UserlistMessage(users));
                }
            }
        });
    }

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