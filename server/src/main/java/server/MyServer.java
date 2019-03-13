package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.*;
import helper.NetworkClassRegistrationHelper;
import message.*;
import requests.DownloadRequest;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.*;

public final class MyServer extends com.esotericsoftware.kryonet.Server {
    private static final String PROPERTYDIR = "games";

    private GameList games;
    private UserList users;
    private File gameDir;


    public MyServer(String gamepath){
        super();
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();

        Settings settings = null;
        try {
            settings = new ServerSettings();
        } catch (IOException e) {
            System.err.println("ERROR: No settings file found.\n");
            System.exit(-9);
        }

        try {
            bind(settings.getServerTcp(), settings.getServerUdp());
        } catch (IOException e) {
            if(e instanceof BindException) {
                System.err.println("ERROR: Address is already bound and/or server is already running.\n");
                System.exit(-10);
            }
        }

        try {
            games = new GameList(PROPERTYDIR);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-11);
        }

        users = new UserList();

        gameDir = new File(gamepath);
        if(!gameDir.isDirectory()) {
            System.err.println("ERROR: Specified gamepath isn't a directory and/or doesn't exist.\n");
            System.exit(-12);
        }

        games.forEach(game -> {
            File gameFile = new File(gameDir, game.getServerFileName());
            if(!gameFile.isFile()) {
                System.err.println("ERROR: No compressed 7-ZIP file found on the server for '" + game.getName() + "'.\n");
                System.exit(-14);
            }
        });
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public MyServer(MyServer server){
        this(server.gameDir.getAbsolutePath());
        this.start();
    }

    public GameList getGames(){
        return games;
    }

    public List<User> getUsersAsList(){
        return users.toList();
    }

    public void updateGames(){
        try {
            games = new GameList(PROPERTYDIR);
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
                    File gameFile = new File(gameDir , request.game.getServerFileName());
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