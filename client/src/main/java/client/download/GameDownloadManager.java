package client.download;

import entities.game.Game;

import java.util.ArrayList;

/**
 * Handles all GameDownloads of the LANClient.
 */
public class GameDownloadManager extends ArrayList<GameDownload>{

    /**
     * Adds this GameDownloadManager to the GameDownload and the GameDownload to this GameDownloadManager.
     *
     * @param gameDownload gameDownloaded that should be handled
     * @return true if GameDownload has been added to the list, else false.
     */
    @Override
    public boolean add(GameDownload gameDownload){
        gameDownload.setManager(this);
        return super.add(gameDownload);
    }

    /**
     * @param game game to search for the download
     * @return GameDownload if the game is downloading or null if the game is not downloading
     */
    public GameDownload getDownload(Game game){
        for(GameDownload download : this){
            if(download.getGame().equals(game))
                return download;
        }
        return null;
    }

    /**
     * @param game game to find in the GameDownloadManager
     * @return true if game is downloading, else false
     */
    public boolean isDownloading(Game game){
        return getDownload(game) != null;
    }

    /**
     * Can be used to get the remaining size of the gamefiles that are currently downloaded.
     *
     * @return remaining download sizes of all running gamefile downloads [bytes].
     */
    public long getSizeRemaining(){
        long remaining = 0;
        //Adds up all remaining download sizes
        for(GameDownload download : this)
            remaining += download.getSizeRemaining();
        return remaining;
    }

}
