package message;

import entities.game.Game;
import entities.user.User;
import entities.user.UserRunServerList;

import java.util.List;

/**
 * {@code UserRunServerMessage} is a class to send the running servers of a {@link Game} of a {@link User}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class UserRunServerMessage {
    public User user;
    public List<Game> servers;

    public UserRunServerMessage(){ }

    public UserRunServerMessage(User user, List<Game> servers){
        this.user = user;
        this.servers = servers;
    }

}
