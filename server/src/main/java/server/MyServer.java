package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import entities.User;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;
import message.*;
import requests.DownloadRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public final class MyServer extends com.esotericsoftware.kryonet.Server {
    private List<Game> games;
    private Map<Integer, User> users;
    private String gamepath;


    public MyServer(String gamepath){
        super();
        this.gamepath = gamepath;
        NetworkClassRegistrationHelper.registerClasses(this);

        int tcp = PropertiesHelper.getServerTcp();
        int udp = PropertiesHelper.getServerUdp();
        try {
            bind(tcp, udp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        games = loadGames();
        printGames();
        users = new HashMap<>();

        registerListener();
    }

    public void updateGames(){
        games = loadGames();
        printGames();
        sendToAllTCP(new GamelistMessage(games));
    }

    public void printGames(){
        games.forEach(Game::print);
    }

    public void printUsers(){
        if(users.isEmpty()) {
            System.out.println("No users logged in.");
            return;
        }
        users.values().forEach(User::print);
    }

    public String getGamepath(){
        return gamepath;
    }


    private List<Game> loadGames(){
        List<Game> games = new ArrayList<>();
        List<Properties> gameProperties = getGameproperties();
        gameProperties.forEach(property -> games.add(new Game(property)));
        return games;
    }

    private List<Properties> getGameproperties(){
        URL url = getClass().getClassLoader().getResource("dummy.properties");
        if(url == null){
            System.err.println("dummy.properties not found in resources.");
            return null;
        }
        File rFile = new File(url.getPath()).getParentFile();
        List<String> properiesNames = new ArrayList<>(Arrays.asList(rFile.list()));
        List<Properties> properties = new ArrayList<>();
        properiesNames.forEach(filename -> {
            if(!filename.equals("dummy.properties"))
                properties.add(PropertiesHelper.getProperties(filename));
        });
        return properties;
    }

    private void registerListener(){
        addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                User user = users.remove(connection.getID());
                if(user == null){
                    System.err.println("Can't remove player with ID: " + connection.getID());
                    return;
                }
                System.out.println(user.getName() + " disconnected.");
                sendToAllTCP(new UserlistMessage(users));
            }
            @Override
            public void received(Connection connection, Object object){
                if(object instanceof LoginMessage){
                    LoginMessage message = (LoginMessage)object;
                    System.out.println(message.user.getName() + " trying to log in ...");
                    if(users.containsKey(connection.getID())){
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userAllreadyLoggedIn));
                        System.err.println(message.user.getName() + " is allready logged in.");
                        return;
                    }
                    users.put(connection.getID(), message.user);
                    System.out.println(message.user.getName() + " logged in and sending games and users.");
                    connection.sendTCP(new GamelistMessage(games));
                    sendToAllTCP(new UserlistMessage(users));
                }
                if(object instanceof UserupdateMessage){
                    UserupdateMessage message = (UserupdateMessage)object;
                    System.out.println("Receiving user update ...");
                    if(!users.containsKey(connection.getID())){
                        connection.sendTCP(new ErrorMessage(ErrorMessage.userNotLoggedIn));
                        System.err.println(message.user.getName() + " not logged in.");
                        return;
                    }
                    User olduser = users.put(connection.getID(), message.user);
                    if(!olduser.getName().equals(users.get(connection.getID()).getName()))
                        System.out.println(olduser.getName() + " changed to " + message.user.getName());
                    sendToAllTCP(new UserlistMessage(users));
                }
                if(object instanceof DownloadRequest){
                    DownloadRequest request = (DownloadRequest)object;
                    if(!games.contains(request.game)){
                        connection.sendTCP(new ErrorMessage(ErrorMessage.gameNotOnServer + request.game.getName()));
                        System.err.println(request.game.getName() + " can't be served for download. Game don't exists.");
                        return;
                    }
                    String ipAddress = connection.getRemoteAddressTCP().getAddress().getHostAddress();
                    String filePath = gamepath + request.game.getFileServer();
                    new FileClient(ipAddress, request.port, filePath, request.game.getName(),
                            users.get(connection.getID()).getName());
                }
            }
        });
    }
}
