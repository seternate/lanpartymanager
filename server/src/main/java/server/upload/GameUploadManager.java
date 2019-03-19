package server.upload;

import java.util.ArrayList;

/**
 * Manages all gameuploads of the LAN-servers.
 */
public class GameUploadManager extends ArrayList<GameUpload> {

    @Override
    public boolean add(GameUpload gameUpload) {
        gameUpload.setManager(this);
        return super.add(gameUpload);
    }

}
