package helper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.Game;
import entities.User;
import requests.DownloadRequest;
import requests.GameinfoRequest;
import requests.GamelistRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public final class NetworkClassRegistrationHelper {

    public static void registerClasses(Server server){
        registerClasses(server.getKryo());
    }

    public static void registerClasses(Client client){
        registerClasses(client.getKryo());
    }

    private static void registerClasses(Kryo kryo){
        //registered entities
        kryo.register(Game.class);
        kryo.register(User.class);

        //registered requests
        kryo.register(DownloadRequest.class);
        kryo.register(GameinfoRequest.class);
        kryo.register(GamelistRequest.class);

        //register base
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(Properties.class);
    }

    private NetworkClassRegistrationHelper(){}
}
