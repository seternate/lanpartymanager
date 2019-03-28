package requests;

import entities.user.User;

public final class CoverDownloadRequest {
    public User user;
    public int port;


    public CoverDownloadRequest(){ }

    public CoverDownloadRequest(User user, int port){
        this.user = user;
        this.port = port;
    }
}
