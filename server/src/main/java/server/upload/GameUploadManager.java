package server.upload;

import java.util.ArrayList;

public class GameUploadManager extends ArrayList<GameUpload> {

    @Override
    public boolean add(GameUpload gameUpload) {
        gameUpload.setManager(this);
        return super.add(gameUpload);
    }

    //TODO
}
