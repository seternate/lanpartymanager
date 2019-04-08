package server.upload;

import entities.game.Game;
import entities.user.User;

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

    /**
     * Searches for a GameUpload from the specified user downloading the specified game.
     *
     * @param user user who downloads the game.
     * @param game game which is downloaded by the user.
     * @return GameUpload to the user of the game, else null.
     */
    public GameUpload get(User user, Game game){
        for(GameUpload upload : this){
            if(upload.getUser().equals(user) && upload.getGame().equals(game))
                return upload;
        }
        return null;
    }

    /**
     * Stops all running uploads.
     */
    public void stopAll(){
        for(GameUpload upload : this)
            upload.stopUpload();
    }
}
