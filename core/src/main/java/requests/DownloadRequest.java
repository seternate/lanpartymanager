package requests;

import entities.game.Game;

/**
 * {@code DownloadRequest} is a class to request the download of a {@link Game}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class DownloadRequest {
    public Game game;
    public int port;

    public DownloadRequest(){}

    public DownloadRequest(Game game, int openPort){
        this.game = game;
        this.port = openPort;
    }

}
