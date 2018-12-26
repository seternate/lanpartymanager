package helper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.Game;

public final class ClassRegistrationHelper {

    public static void registerClasses(Server server){
        registerClasses(server.getKryo());
    }

    public static void registerClasses(Client client){
        registerClasses(client.getKryo());
    }

    private static void registerClasses(Kryo kryo){
        kryo.register(Game.class);
    }

    private ClassRegistrationHelper(){}
}
