package requests;

import entities.user.User;

/**
 * {@code ImageDownloadRequest} is a class to request the download of all images from the {@code LANServer}.
 *
 * @author Levin Jeck
 * @version 2.0
 * @since 1.0
 */
public final class ImageDownloadRequest {
    public String ip;
    public int port;

    public ImageDownloadRequest(){ }

    public ImageDownloadRequest(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

}
