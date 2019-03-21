package client.download;

import entities.game.Game;

import java.util.ArrayList;

//TODO
public final class GameDownloadManager extends ArrayList<GameDownload>{

    @Override
    public boolean add(GameDownload gameDownload){
        gameDownload.setManager(this);
        return super.add(gameDownload);
    }

    public GameDownload getDownload(Game game){
        for(GameDownload download : this){
            if(download.getGame().equals(game))
                return download;
        }
        return null;
    }

    public boolean isDownloading(Game game){
        return getDownload(game) != null;
    }

    public long getSizeRemaining(){
        return 0;
    }

}
