package message;

import entities.game.Game;
import entities.user.User;
import entities.user.UserRunServerList;

import java.util.List;

public final class UserRunServerMessage {
    public User user;
    public List<Game> servers;
    public UserRunServerList userrunservers;

    public UserRunServerMessage(){ }

    public UserRunServerMessage(User user, List<Game> servers){
        this.user = user;
        this.servers = servers;
    }

    public UserRunServerMessage(UserRunServerList userrunservers){
        this.userrunservers = userrunservers;
    }
}
