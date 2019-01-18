package client;

import entities.Game;

import java.util.ArrayList;
import java.util.List;

final class DownloadManager {
    private List<Download> downloads;


    DownloadManager(){
        downloads = new ArrayList<>();
    }

    void add(Download download){
        downloads.add(download);
        download.setManager(this);
    }

    void remove(Download download){
        downloads.remove(download);
    }

    Download getDownloadStatus(Game game){
        for(Download download : downloads){
            if(download.game.equals(game))
                return download;
        }
        return null;
    }

}
