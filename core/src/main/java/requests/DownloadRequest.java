package requests;

import entities.game.Game;

public final class DownloadRequest {
    public Game game;
    public int port;


    @SuppressWarnings("unused")
    public DownloadRequest(){}

    public DownloadRequest(Game game, int openPort){
        this.game = game;
        this.port = openPort;
    }
}
