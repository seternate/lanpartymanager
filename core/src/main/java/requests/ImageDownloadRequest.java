package requests;

import entities.user.User;

/**
 * {@code ImageDownloadRequest} is a class to request the download of all images from the {@code LANServer}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class ImageDownloadRequest {
    public User user;
    public int port;

    public ImageDownloadRequest(){ }

    public ImageDownloadRequest(User user, int port){
        this.user = user;
        this.port = port;
    }

}
