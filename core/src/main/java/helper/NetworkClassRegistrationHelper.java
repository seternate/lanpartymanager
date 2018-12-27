package helper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.Game;
import requests.downloadRequest;
import requests.gameinfoRequest;
import requests.gamelistRequest;
import requests.userlistRequest;

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
        kryo.register(downloadRequest.class);
        kryo.register(gameinfoRequest.class);
        kryo.register(gamelistRequest.class);
        kryo.register(userlistRequest.class);
    }

    private NetworkClassRegistrationHelper(){}
}
