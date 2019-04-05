package helper.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.game.Game;
import entities.game.GameList;
import entities.settings.ClientSettings;
import entities.user.User;
import entities.user.UserList;
import entities.user.UserRunGamesList;
import entities.user.UserRunServerList;
import message.*;
import requests.CoverDownloadRequest;
import requests.DownloadRequest;

import java.util.ArrayList;

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
        kryo.register(GameList.class);
        kryo.register(User.class);
        kryo.register(UserList.class);
        kryo.register(UserRunGameMessage.class);
        kryo.register(UserRunGamesList.class);
        kryo.register(UserRunServerMessage.class);
        kryo.register(UserRunServerList.class);
        kryo.register(ClientSettings.class);
        kryo.register(DownloadRequest.class);
        kryo.register(DownloadStopMessage.class);
        kryo.register(CoverDownloadRequest.class);
        kryo.register(LoginMessage.class);
        kryo.register(ErrorMessage.class);
        kryo.register(GamelistMessage.class);
        kryo.register(UserlistMessage.class);
        kryo.register(UserupdateMessage.class);
        kryo.register(ArrayList.class);
    }

}
