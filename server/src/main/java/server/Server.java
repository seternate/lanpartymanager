package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;
import requests.gamelistRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public final class Server {

    /**
     * <code>String</code> representation for a key from <code>settings.properties</code>.
     */
    private final static String SERVERTCP = "servertcp",
                                SERVERUDP = "serverudp",
                                GAMEPATH = "gamepath";
    private static Server serverObj = null;

    /**
     * Setup the game-streaming server for a lan-party, if none is running.
     * <p>
     * {@link #start()} call required.
     *
     * @return the running {@link Server} object.
     */
    public static Server build(){
        if(serverObj == null){
            serverObj = new Server();
        }
        return serverObj;
    }
    /**
     * Rebuilds the Server.
     * <p>
     * {@link #start()} call required.
     *
     * @throws NullPointerException no server is running.
     * @return a new initialized {@link Server} object.
     */
    public static Server rebuild(){
        if(serverObj != null){
            serverObj = new Server();
            return serverObj;
        }
        throw new NullPointerException("No server running.");
    }
    //ToDo: Server distroy


    /**
     * {@link com.esotericsoftware.kryonet.Server} object used for the server base functionality.
     */
    private com.esotericsoftware.kryonet.Server server;
    /**
     * {@link Properties} used by the game-streaming server.
     * Any changes requires a {@link #rebuild()} method call.
     */
    private final Properties properties = PropertiesHelper.getProperties();
    /**
     * <code>Game-list</code> with all available games on the {@link Server}.
     */
    private ArrayList<Game> gameList;

    /**
     * Constructs a new {@link Server} object.
     * Also registering classes and adding the needed listener for the game-streaming server.
     * <p>
     * {@link #start()} call required.
     *
     * @see com.esotericsoftware.kryonet.Server
     */
    private Server(){
        //Initialize the Server-Object
        server = new com.esotericsoftware.kryonet.Server();

        //Register classes needed for communication
        NetworkClassRegistrationHelper.registerClasses(server);

        //Bind the ports for the server
        int tcp = Integer.valueOf(properties.getProperty(SERVERTCP));
        int udp = Integer.valueOf(properties.getProperty(SERVERUDP));
        try {
            server.bind(tcp, udp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //register the needed listener to handle the incoming client-requests
        this.registerListener();

        //build the game-list
        gameList = GameListBuilder.build();
    }
    /**
     * Starts the {@link Server}.
     */
    public void start(){
        server.start();
    }
    /**
     * Registers the needed <code>listener</code> for the {@link Server} to listen to incoming <code>client</code>
     * requests.
     *
     * @see com.esotericsoftware.kryonet.Server
     */
    private void registerListener(){
        server.addListener(new Listener() {
            public void received(Connection connection, Object object){
                if(object instanceof gamelistRequest){
                    connection.sendTCP(gameList);
                }
            }
        });
    }
    /**
     * @return <code>game-list</code> of the {@link Server}.
     */
    public ArrayList<Game> getGamelist(){
        return this.gameList;
    }
    /**
     * Refreshes <code>game-list</code> of the {@link Server}.
     */
    public void refreshGamelist(){
        gameList = GameListBuilder.build();
    }

}
