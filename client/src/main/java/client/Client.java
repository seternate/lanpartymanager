package client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import entities.User;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private com.esotericsoftware.kryonet.Client client;
    private User user;
    private ArrayList<Game> gamelist;
    private HashMap<Integer, User> userlist;

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
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
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
                }else if(object instanceof HashMap){
                    HashMap hashmap = (HashMap)object;
                    if(hashmap.containsKey(connection.getID())){
                        System.out.println("Received user-list.");
                        userlist = hashmap;
                    }
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
            //Todo: Download logic.
        }
        return false;
    }

    public boolean downloadGame(String gameName){
        //Todo: get Game-object with gameName-string.
        return false;
    }
}
