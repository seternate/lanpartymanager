package client;

import entities.Game;

import java.util.ArrayList;
import java.util.List;

public class DownloadManager {
    private List<Download> downloads;


    public DownloadManager(){
        downloads = new ArrayList<>();
    }

    public void add(Download download){
        downloads.add(download);
        download.setManager(this);
    }

    public void remove(Download download){
        downloads.remove(download);
    }

    public Download getDownloadStatus(Game game){
        for(Download download : downloads){
            if(download.game.equals(game))
                return download;
        }
        return null;
    }

}
