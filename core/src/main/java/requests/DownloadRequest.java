package requests;

import entities.Game;

/**
 * Functions as a Request object and has no function at all. It provides the {@link Game} which should be downloaded.
 */
public class DownloadRequest {
    public Game game;
    public int port;

    public DownloadRequest(){}

    public DownloadRequest(Game game, int openPort){
        this.game = game;
        this.port = openPort;
    }
}
