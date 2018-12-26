package main;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.GameList;
import helper.ClassRegistrationHelper;
import helper.PropertiesHelper;
import requests.gameListRequest;

import java.io.IOException;
import java.util.Properties;

public final class Server {

    private final static String servertcp = "servertcp",
                                serverudp = "serverudp";
    /**
     * Properties used by the game-streaming server.
     * Any changes require a server restart.
     */
    private final static Properties properties = PropertiesHelper.getProperties();
    /**
     * Holding the List of Games provided by the server.
     */
    private static GameList gameList = null;
    private static Server serverObj = null;

    /**
     * Setup and start the game-streaming server for a lan-party, if none is running.
     *
     * @return <code>true</code> if the server was created and started, <code>false</code> if the server is already started.
     */
    static boolean start(){
        if(serverObj == null){
            serverObj = new Server();
            return true;
        }
        return false;
    }
    /**
     *
     * @return <code>GameList</code> object with all games provided from this server.
     */
    public static GameList getGamelist(){
        return gameList;
    }
    /**
     * Refreshes the <code>GameList</code> object of this server.
     */
    public static void refreshGamelist(){
        gameList = new GameList();
    }


    private com.esotericsoftware.kryonet.Server server = null;

    /**
     * Registers the needed <code>listener</code> for the game-streaming server.
     */
    private void registerListener(){
        server.addListener(new Listener() {
            public void received(Connection connection, Object object){
                if(object instanceof gameListRequest){

                }
            }
        });
    }
    /**
     * Constructs a new <code>Server</code> object and starts the server.
     * Also registering classes and adding the needed listener for the game-streaming server.
     */
    private Server(){
        server = new com.esotericsoftware.kryonet.Server();

        //Register classes
        ClassRegistrationHelper.registerClasses(server);

        //Bind the ports for the server
        int tcp = Integer.valueOf(properties.getProperty(servertcp));
        int udp = Integer.valueOf(properties.getProperty(serverudp));
        try {
            server.bind(tcp, udp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Register the server listener
        this.registerListener();
        refreshGamelist();
        server.start();
    }
}
