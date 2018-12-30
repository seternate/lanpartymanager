package requests;

import entities.Game;

public class GamesizeRequest {
    public Game game;

    public GamesizeRequest(){}

    public GamesizeRequest(Game game){
        this.game = game;
    }
}
