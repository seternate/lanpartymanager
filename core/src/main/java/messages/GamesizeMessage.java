package messages;

import entities.Game;

public class GamesizeMessage {

    public long filesize;
    public Game game;

    public GamesizeMessage(){}

    public GamesizeMessage(Game game, long filesize){
        this.game = game;
        this.filesize = filesize;
    }
}
