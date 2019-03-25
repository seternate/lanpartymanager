package message;

import entities.game.Game;
import entities.user.User;
import entities.user.UserRunGamesList;

import java.util.List;

public class UserRunGameMessage {
    public User user;
    public List<Game> games;
    public UserRunGamesList userrungames;


    public UserRunGameMessage(){ }

    public UserRunGameMessage(User user, List<Game> games){
        this.user = user;
        this.games = games;
    }

    public UserRunGameMessage(UserRunGamesList userrungames){
        this.userrungames = userrungames;
    }

}
