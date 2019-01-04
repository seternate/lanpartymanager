package requests;

import entities.Game;

public class DownloadRequest {
    public Game game;
    public int port;


    public DownloadRequest(){}

    public DownloadRequest(Game game, int openPort){
        this.game = game;
        this.port = openPort;
    }
}
