package message;

import entities.Game;

import java.util.List;

public final class GamelistMessage {
    public List<Game> games;


    @SuppressWarnings("unused")
    public GamelistMessage(){ }

    public GamelistMessage(List<Game> games){
        this.games = games;
    }
}
