package client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import entities.Status;
import entities.User;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;
import message.GamelistMessage;
import message.LoginMessage;
import message.UserlistMessage;
import message.UserupdateMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class MyClient extends com.esotericsoftware.kryonet.Client {
    private List<Game> games;
    private Map<Integer, User> users;
    private User user;
    private Status status;


    public MyClient(){
        super();
        user = new User();
        status = new Status();
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();
        start();
    }

    @Override
    public void start(){
        super.start();
        new Thread(() -> {
            while(true){
                if(isConnected()) {
                    status.serverConnection = true;
                    status.serverIP = getRemoteAddressTCP().getHostString();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    status.serverConnection = false;
                    connect();
                }
            }
        }).start();
    }

    public boolean updateUser(User user){
        boolean changed = false;
        changed = changeUsername(user.getName()) || changeGamepath(user.getGamepath());
        return changed;
    }

    public Status getStatus(){
        return status;
    }

    public User getUser(){
        return user;
    }



    private boolean changeUsername(String username){
        if(user.getName().equals(username)) {
            System.err.println("Username is allready " + username);
            return false;
        }else if(!isConnected()){
            System.err.println("No connection to the server.");
        }
        System.out.println("Changed name from " + user.getName() + " to " + username);
        user.setName(username);
        sendTCP(new UserupdateMessage(user));
        return true;
    }

    private boolean changeGamepath(String gamepath){
        if(user.getGamepath().equals(gamepath)) {
            System.err.println("Gamepath is allready " + gamepath);
            return false;
        }else if(!isConnected()){
            System.err.println("No connection to the server.");
        }
        System.out.println("Changed path from " + user.getGamepath() + " to " + gamepath);
        user.setGamepath(gamepath);
        sendTCP(new UserupdateMessage(user));
        return true;
    }

    private void registerListener(){
        addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                sendTCP(new LoginMessage(user));
                System.out.println("Logged in.");
            }
            @Override
            public void received (Connection connection, Object object) {
                if(object instanceof GamelistMessage){
                    GamelistMessage message = (GamelistMessage)object;
                    games = message.games;
                    System.out.println("Received games.");
                }
                if(object instanceof UserlistMessage){
                    UserlistMessage message = (UserlistMessage)object;
                    users = message.users;
                    System.out.println("Received users.");
                }
            }
        });
    }

    private void connect(){
        int tcp = PropertiesHelper.getServerTcp();
        int udp = PropertiesHelper.getServerUdp();
        InetAddress address = discoverHost(udp, 5000);
        if(address != null) {
            try {
                connect(500, address, tcp, udp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*

    private int downloadGame(Game game){
        if(!game.isUpToDate()){
            File sFile = new File(PropertiesHelper.getGamepath());
            if(game.getSize() > sFile.getFreeSpace()) return 1;
            int openport = getOpenPort();
            FileServer fServer = new FileServer(openport, game, game.getSize());
            System.out.println("Requested to download " + game.getName() + ".");
            client.sendTCP(new DownloadRequest(game, openport));
            return 0;
        }
        return 2;
    }

    public int downloadGame(String gameName){
        for (Game game : gamelist) {
            if(game.getName().equals(gameName)){
                return downloadGame(game);
            }
        }
        return 3;
    }

    private int getOpenPort(){
        ServerSocket server = null;
        try {
            server = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int freeport = server.getLocalPort();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freeport;
    }

    public ArrayList<Game> getGamelist(){
        return this.gamelist;
    }

    public HashMap<Integer, User> getUserlist(){
        return this.userlist;
    }

    public String status(){
        return status;
    }

    public void update(){
        this.user = new User();
        File dFile = new File(PropertiesHelper.getGamepath());
        if(!dFile.exists()) dFile.mkdirs();
        client.sendTCP(user);
    }
    */
}
