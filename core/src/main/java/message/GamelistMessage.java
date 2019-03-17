package message;

import entities.GameList;

public final class GamelistMessage {
    public GameList games;


    @SuppressWarnings("unused")
    public GamelistMessage(){ }

    public GamelistMessage(GameList games){
        this.games = games;
    }
}
