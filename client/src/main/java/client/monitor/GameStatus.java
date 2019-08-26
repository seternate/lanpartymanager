package client.monitor;

import entities.game.Game;
import javafx.beans.property.*;

import java.util.Locale;

/**
 * {@code GameStatus} handles the status of an {@link Game}. Available information are the version, up-to-date state,
 * download- and extractionprogress.
 *
 * @author Levin Jeck
 * @version 2.0
 * @since 1.0
 */
public class GameStatus {
    private Game game;
    private boolean playable, version, update;
    private BooleanProperty downloading, extracting, local, running;
    private DoubleProperty downloadProgress, extractionProgress;
    private StringProperty downloadSpeed;


    /**
     * Creates the {@code GameStatus} with all fields set to {@code false} or {@code 0}.
     *
     * @since 1.0
     */
    public GameStatus(Game game){
        this.game = game;
        playable = false;
        local = new SimpleBooleanProperty(false);
        version = true;
        update = false;
        downloading = new SimpleBooleanProperty(false);
        extracting = new SimpleBooleanProperty(false);
        running = new SimpleBooleanProperty(false);
        downloadProgress = new SimpleDoubleProperty(0.);
        extractionProgress = new SimpleDoubleProperty(0.);
        downloadSpeed = new SimpleStringProperty("");
    }

    /**
     * @return <b>true</b> if the {@link Game} is up-to-date, else <b>false</b>
     * @since 1.0
     */
    public boolean isPlayable() {
        return playable;
    }

    /**
     * @param playable <b>true</b> if the {@link Game} is up-to-date, else <b>false</b>
     * @since 1.0
     */
    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    /**
     * @return <b>true</b> if the {@link Game} is local available, else <b>false</b>
     * @since 1.0
     */
    public boolean isLocal() {
        return local.get();
    }

    /**
     * @param local <b>true</b> if the {@link Game} is local available, else <b>false</b>
     * @since 1.0
     */
    public void setLocal(boolean local) {
        this.local.set(local);
    }

    public BooleanProperty getLocalProperty(){
        return local;
    }

    /**
     * @return <b>true</b> if the version of the {@link Game} can be determined, else <b>false</b>
     * @since 1.0
     */
    public boolean isVersion() {
        return version;
    }

    /**
     * @param version <b>true</b> if the version of the {@link Game} can be determined, else <b>false</b>
     * @since 1.0
     */
    public void setVersion(boolean version) {
        this.version = version;
    }

    /**
     * @return <b>true</b> if the {@link Game} has to be updated, else <b>false</b>
     * @since 1.0
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * @param update <b>true</b> if the {@link Game} has to be updated, else <b>false</b>
     * @since 1.0
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /**
     * @return <b>true</b> if the {@link Game} is downloading, else <b>false</b>
     * @since 1.0
     */
    public boolean isDownloading() {
        return downloading.get();
    }

    /**
     * @param downloading <b>true</b> if the {@link Game} is downloading, else <b>false</b>
     * @since 1.0
     */
    public void setDownloading(boolean downloading) {
        this.downloading.set(downloading);
    }

    public BooleanProperty getDownloadingProperty(){
        return downloading;
    }

    /**
     * @return <b>true</b> if the {@link Game} is extracting, else <b>false</b>
     * @since 1.0
     */
    public boolean isExtracting() {
        return extracting.get();
    }

    /**
     * @param extracting <b>true</b> if the {@link Game} is extracting, else <b>false</b>
     * @since 1.0
     */
    public void setExtracting(boolean extracting) {
        this.extracting.set(extracting);
    }

    public BooleanProperty getExtractingProperty(){
        return extracting;
    }

    /**
     * @return progress of the download in a range from {@code 0} to {@code 1}
     * @since 1.0
     */
    public double getDownloadProgress() {
        return downloadProgress.get();
    }

    /**
     * @param downloadProgress progress of the download in a range from {@code 0} to {@code 1}
     * @since 1.0
     */
    public void setDownloadProgress(double downloadProgress) {
        if(downloadProgress > 1.)
            downloadProgress = 1.;
        else if(downloadProgress < 0.)
            downloadProgress = 0.;
        this.downloadProgress.set(downloadProgress);
    }

    public DoubleProperty getDownloadProgressProperty(){
        return downloadProgress;
    }

    /**
     * @return progress of the extraction in a range from {@code 0} to {@code 1}
     * @since 1.0
     */
    public double getExtractionProgress() {
        return extractionProgress.get();
    }

    /**
     * @param extractionProgress progress of the download in a range from {@code 0} to {@code 1}
     * @since 1.0
     */
    public void setExtractionProgress(double extractionProgress) {
        if(extractionProgress > 1.)
            extractionProgress = 1.;
        else if(extractionProgress < 0.)
            extractionProgress = 0.;
        this.extractionProgress.set(extractionProgress);
    }

    public DoubleProperty getExtractionProgressProperty(){
        return extractionProgress;
    }

    /**
     * @return average downloadspeed of the download
     * @since 1.0
     */
    public String getDownloadSpeed(){
        return downloadSpeed.get();
    }

    /**
     * @param downloadSpeed average downloadspeed of the download
     * @since 1.0
     */
    public void setDownloadSpeed(long downloadSpeed){
        String speed = String.format(Locale.ENGLISH, "%.0f MB/sec", (float)downloadSpeed/1048576.);
        this.downloadSpeed.set(speed);
    }

    public StringProperty getDownloadSpeedProperty(){
        return downloadSpeed;
    }

    /**
     * @return <b>true</b> if the {@link Game} is running, else <b>false</b>
     * @since 1.0
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * @param running <b>true</b> if the {@link Game} is running, else <b>false</b>
     * @since 1.0
     */
    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public BooleanProperty getRunningProperty(){
        return running;
    }

    public Game getGame(){
        return game;
    }

}
