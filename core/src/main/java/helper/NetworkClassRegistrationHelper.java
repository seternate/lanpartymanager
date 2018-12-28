package helper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.Game;
import requests.DownloadRequest;
import requests.GameinfoRequest;
import requests.GamelistRequest;
import requests.UserlistRequest;

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

        //registered requests
        kryo.register(DownloadRequest.class);
        kryo.register(GameinfoRequest.class);
        kryo.register(GamelistRequest.class);
        kryo.register(UserlistRequest.class);
    }

    private NetworkClassRegistrationHelper(){}
}
