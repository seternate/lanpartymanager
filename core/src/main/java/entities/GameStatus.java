package entities;

public final class GameStatus {
    public boolean playable, download, version, update, downloading, unzipping;
    public double downloadProgress, unzipProgress;

    public GameStatus(){
        playable = false;
        download = false;
        version = true;
        update = false;
        downloading = false;
        unzipping = false;
        downloadProgress = 0;
        unzipProgress = 0;
    }

}
