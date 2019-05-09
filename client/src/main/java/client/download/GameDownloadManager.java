package client.download;

import entities.game.Game;

import java.util.ArrayList;

/**
 * {@code GameDownloadManager} manages all added {@link GameDownload}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class GameDownloadManager extends ArrayList<GameDownload>{

    /**
     * Adds the {@code gameDownload} to the {@code GameDownloadManager} and calls
     * {@link GameDownload#setManager(GameDownloadManager)}.
     *
     * @param gameDownload {@link GameDownload} to add
     * @return <b>true</b> as in {@link ArrayList#add(Object)}
     * @since 1.0
     */
    @Override
    public boolean add(GameDownload gameDownload){
        gameDownload.setManager(this);
        return super.add(gameDownload);
    }

    /**
     * @param game {@link Game} to look for its {@code GameDownload}
     * @return {@code null} if no {@code GameDownload} of the {@code game} can be found
     * @since 1.0
     */
    public GameDownload getDownload(Game game){
        for(GameDownload download : this){
            if(download.getGame().equals(game))
                return download;
        }
        return null;
    }

    /**
     * @param game {@link Game} to get the downloadstatus from
     * @return <b>true</b> if the {@code game} is downloading, else <b>false</b>
     * @since 1.0
     */
    public boolean isDownloading(Game game){
        return getDownload(game) != null;
    }

    /**
     * Adds up the remaining filesizes to download of all running {@link GameDownload}.
     *
     * @return remaining size of all running {@code GameDownload} [bytes]
     * @see GameDownload#getSizeRemaining()
     * @since 1.0
     */
    public long getSizeRemaining(){
        long remaining = 0;
        //Adds up all remaining download sizes
        for(GameDownload download : this)
            remaining += download.getSizeRemaining();
        return remaining;
    }

    /**
     * Stops the download/extraction of all running {@link GameDownload}.
     *
     * @since 1.0
     */
    public void stopAll(){
        for(GameDownload download : this)
            download.stopDownloadUnzip();
    }

    /**
     * Stops the download/extraction of the {@code games} {@link GameDownload}.
     *
     * @param game {@link Game} to stop downloading or extracting
     * @since 1.0
     */
    public void stop(Game game){
        this.getDownload(game).stopDownloadUnzip();
    }

}
