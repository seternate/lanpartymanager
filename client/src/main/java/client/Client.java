package client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import entities.User;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;
import requests.GamelistRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class Client {
    private com.esotericsoftware.kryonet.Client client;
    private User user;
    private ArrayList<Game> gamelist;
    private ArrayList<User> userlist;

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
                client.sendTCP(new GamelistRequest());
            }
            @Override
            public void received (Connection connection, Object object) {
                if(object instanceof ArrayList){
                    ArrayList list = (ArrayList)object;
                    if(list.get(0) instanceof Game){
                        gamelist = list;
                    }else if(list.get(0) instanceof User){
                        userlist = list;
                    }
                }
            }
        });
    }

    public ArrayList<Game> getGamelist(){
        return this.gamelist;
    }

    public ArrayList<User> getUserlist(){
        return this.userlist;
    }

    public void download(Game game){
        //Todo
    }

    public void getOpenGameServers(){

    }

    public void getOpenGameServer(Game game){

    }
}
