package client;

import java.util.ArrayList;
import java.util.List;

//Todo
public class DownloadManager {
    private List<Download> downloads;

    public DownloadManager(){
        downloads = new ArrayList<>();
    }

    public void add(Download download){
        downloads.add(download);
    }

}
