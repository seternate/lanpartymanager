package helper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.Game;
import entities.User;
import requests.DownloadRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * <code>Helper class</code> for registering classes for network-activity. Every object that is send through the network
 * must be registered first.
 * <p>
 * No object can be created from this class, because it only functions as a <code>helper class</code>.
 *
 * @see com.esotericsoftware.kryonet.Server
 * @see com.esotericsoftware.kryonet.Client
 */
public abstract class NetworkClassRegistrationHelper {

    /**
     * Registers the needed classes for networking to the server.
     *
     * @param server registering the classes to.
     *
     * @see #registerClasses(Kryo)
     */
    public static void registerClasses(Server server){
        registerClasses(server.getKryo());
    }
    /**
     * Registers the needed classes for networking to the client.
     *
     * @param client registering the classes to.
     *
     * @see #registerClasses(Kryo)
     */
    public static void registerClasses(Client client){
        registerClasses(client.getKryo());
    }
    /**
     * Registers the needed classes for networking.
     *
     * @param kryo any {@link com.esotericsoftware.kryo.Kryo} object (e.g. {@link com.esotericsoftware.kryonet.Server}
     *             or {@link com.esotericsoftware.kryonet.Client}) to which the classes for networking should be registered.
     *
     * @see #registerClasses(Client)
     * @see #registerClasses(Server)
     */
    private static void registerClasses(Kryo kryo){
        //registered entities
        kryo.register(Game.class);
        kryo.register(User.class);

        //registered requests
        kryo.register(DownloadRequest.class);

        //register base
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(Properties.class);
    }
}
