package client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import entities.User;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;
import requests.DownloadRequest;

import java.io.File;
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
    private String status;

    public Client(){
        client = new com.esotericsoftware.kryonet.Client();
        NetworkClassRegistrationHelper.registerClasses(client);
        this.registerListener();
        status = "Client initialized.";
    }

    public void start(){
        new Thread(client).start();
        status = "Client started.";
        new Thread(() -> {
            while(true){
                if(client.isConnected()) {
                    status = "Client connected with Server: " + client.getRemoteAddressTCP().getAddress().getHostAddress();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    status = "Client searching for servers ...";
                    connect();
                }
            }
        }).start();
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
                    }
                }
            }
        });
    }

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
}
