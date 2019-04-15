package entities.game;

/**
 * Class for Client GUI communication about a games status on the system.
 */
public final class GameStatus {
    private boolean playable, local, version, update, downloading, unzipping, running;
    private double downloadProgress, unzipProgress;
    private String downloadSpeed;


    /**
     * Creates all fields in standard mode with false and 0.
     */
    public GameStatus(){
        playable = false;
        local = false;
        version = true;
        update = false;
        downloading = false;
        unzipping = false;
        running = false;
        downloadProgress = 0.;
        unzipProgress = 0.;
        downloadSpeed = "";
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public boolean isUnzipping() {
        return unzipping;
    }

    public void setUnzipping(boolean unzipping) {
        this.unzipping = unzipping;
    }

    public double getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(double downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public double getUnzipProgress() {
        return unzipProgress;
    }

    public void setUnzipProgress(double unzipProgress) {
        this.unzipProgress = unzipProgress;
    }

    public String getDownloadSpeed(){
        return downloadSpeed;
    }

    public void setDownloadSpeed(String downloadSpeed){
        this.downloadSpeed = downloadSpeed;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
