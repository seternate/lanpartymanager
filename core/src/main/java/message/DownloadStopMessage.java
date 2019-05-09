package message;

import entities.game.Game;
import entities.user.User;

/**
 * {@code DownloadStopMessage} is a class to notify the receiver, that the download has been stopped.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class DownloadStopMessage {
    public User user;
    public Game game;

    public DownloadStopMessage(){ }

    public DownloadStopMessage(User user, Game game){
        this.user = user;
        this.game = game;
    }

}
