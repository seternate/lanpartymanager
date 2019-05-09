package message;

import entities.game.Game;
import entities.user.User;
import entities.user.UserRunGamesList;

import java.util.List;

/**
 * {@code UserRunGamesMessage} is a class to send the running {@link Game} of a {@link User}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class UserRunGameMessage {
    public User user;
    public List<Game> games;

    public UserRunGameMessage(){ }

    public UserRunGameMessage(User user, List<Game> games){
        this.user = user;
        this.games = games;
    }

}
