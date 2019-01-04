package message;

import entities.Game;

import java.util.List;

public class GamelistMessage {
    public List<Game> games;


    public GamelistMessage(){ }

    public GamelistMessage(List<Game> games){
        this.games = games;
    }
}
