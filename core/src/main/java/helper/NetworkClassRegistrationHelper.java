package helper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.Game;
import entities.User;
import message.*;
import requests.DownloadRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public abstract class NetworkClassRegistrationHelper {

    public static void registerClasses(Server server){
        registerClasses(server.getKryo());
    }

    public static void registerClasses(Client client){
        registerClasses(client.getKryo());
    }

    private static void registerClasses(Kryo kryo){
        kryo.register(Game.class);
        kryo.register(Game.Version.class);
        kryo.register(User.class);
        kryo.register(DownloadRequest.class);
        kryo.register(LoginMessage.class);
        kryo.register(ErrorMessage.class);
        kryo.register(GamelistMessage.class);
        kryo.register(UserlistMessage.class);
        kryo.register(UserupdateMessage.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(Properties.class);
    }
}
