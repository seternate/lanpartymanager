package client;

import entities.game.Game;

import java.util.ArrayList;

//TODO
final class GameDownloadManager extends ArrayList<GameDownload>{

    @Override
    public boolean add(GameDownload gameDownload){
        gameDownload.setManager(this);
        return super.add(gameDownload);
    }

    public GameDownload getDownload(Game game){
        for(GameDownload download : this){
            if(download.game.equals(game))
                return download;
        }
        return null;
    }

    //TODO: boolean method for game download check
}
