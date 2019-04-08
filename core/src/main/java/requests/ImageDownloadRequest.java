package requests;

import entities.user.User;

public final class ImageDownloadRequest {
    public User user;
    public int port;


    public ImageDownloadRequest(){ }

    public ImageDownloadRequest(User user, int port){
        this.user = user;
        this.port = port;
    }
}
