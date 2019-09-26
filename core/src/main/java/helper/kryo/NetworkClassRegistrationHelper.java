package helper.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import entities.game.Game;
import entities.game.GameList;
import entities.game.serverparameters.*;
import entities.settings.ClientSettings;
import entities.user.User;
import entities.user.UserList;
import entities.user.UserRunGamesList;
import entities.user.UserRunServerList;
import message.*;
import requests.ImageDownloadRequest;
import requests.DownloadRequest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * {@code NetworkClassRegistrationHelper} handles the registration of the {@code Classes} to the {@link Client} and
 * {@link Server}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public abstract class NetworkClassRegistrationHelper {

    /**
     * Registers {@code Classes} to a {@link Server}.
     *
     * @param server {@code Server} to register {@code Classes to}
     * @since 1.0
     */
    public static void registerClasses(Server server){
        registerClasses(server.getKryo());
    }

    /**
     * Registers {@code Classes} to a {@link Client}.
     *
     * @param client {@code Client} to register {@code Classes} to
     * @since 1.0
     */
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
        kryo.register(ImageDownloadRequest.class);
        kryo.register(LoginMessage.class);
        kryo.register(ErrorMessage.class);
        kryo.register(GamelistMessage.class);
        kryo.register(UserlistMessage.class);
        kryo.register(UserupdateMessage.class);
        kryo.register(ArrayList.class);
        kryo.register(ServerParameters.class);
        kryo.register(ServerParameterNumber.class);
        kryo.register(ServerParameterLiteral.class);
        kryo.register(ServerParameterDropdown.class);
        kryo.register(ServerParameterBoolean.class);
        kryo.register(ServerParameterType.class);
        kryo.register(HashMap.class);
    }

}
