package client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import entities.User;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;
import main.Main;
import messages.GamesizeMessage;
import requests.DownloadRequest;
import requests.GamesizeRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private com.esotericsoftware.kryonet.Client client;
    private User user;
    private ArrayList<Game> gamelist;
    private HashMap<Integer, User> userlist;
    private int init = 0;

    public Client(){
        client = new com.esotericsoftware.kryonet.Client();
        user = new User();
        NetworkClassRegistrationHelper.registerClasses(client);
        this.registerListener();
    }

    public void start(){
        new Thread(client).start();
        new Thread(() -> {
            while(true){
                if(client.isConnected()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    connect();
                }
            }
        }).start();
        synchronized (this){
            try {
                wait();
                init = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect(){
        int tcp = Integer.valueOf(PropertiesHelper.getServerTcp());
        int udp = Integer.valueOf(PropertiesHelper.getServerUdp());
        InetAddress address = client.discoverHost(udp, 5000);
        if(address != null) {
            try {
                client.connect(5000, address, tcp, udp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerListener(){
        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                client.sendTCP(user);
                System.out.println("Connected to Server.");
            }
            @Override
            public void received (Connection connection, Object object) {
                if(object instanceof ArrayList){
                    ArrayList list = (ArrayList)object;
                    if(list.get(0) instanceof Game){
                        gamelist = list;
                        System.out.println("Received game-list.");
                    }
                }
                if(object instanceof HashMap){
                    HashMap hashmap = (HashMap)object;
                    if(hashmap.containsKey(connection.getID())){
                        System.out.println("Received user-list.");
                        userlist = hashmap;
                        if(init == 0){
                            synchronized (Main.client){
                                Main.client.notifyAll();
                                init = 1;
                            }
                        }

                    }
                }
                if(object instanceof GamesizeMessage){
                    GamesizeMessage gsMessage = (GamesizeMessage)object;
                    int openport = getOpenPort();
                    FileServer fServer = new FileServer(openport, gsMessage.game, gsMessage.filesize);
                    client.sendTCP(new DownloadRequest(gsMessage.game, openport));
                }
            }
        });
    }

    public ArrayList<Game> getGamelist(){
        return this.gamelist;
    }

    public HashMap<Integer, User> getUserlist(){
        return this.userlist;
    }

    public boolean downloadGame(Game game){
        if(!game.isUpToDate()){
            client.sendTCP(new GamesizeRequest(game));
            return true;
        }
        return false;
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

    public boolean downloadGame(String gameName){

        return false;
    }
}
