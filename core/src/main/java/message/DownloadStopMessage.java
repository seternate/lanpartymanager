package message;

import entities.game.Game;
import entities.user.User;

public class DownloadStopMessage {
    public User user;
    public Game game;


    public DownloadStopMessage(){ }

    public DownloadStopMessage(User user, Game game){
        this.user = user;
        this.game = game;
    }
}
