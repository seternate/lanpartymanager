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

public class MyClient extends com.esotericsoftware.kryonet.Client {




    private User user;
    private ArrayList<Game> gamelist;
    private HashMap<Integer, User> userlist;
    private String status;

    public MyClient(){
        super();
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

    private void registerListener(){
        addListener(new Listener() {
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
