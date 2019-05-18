package server.upload;

import entities.game.Game;
import entities.user.User;

import java.util.ArrayList;

/**
 * {@code GameUploadManager} manages all added {@link GameUpload}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class GameUploadManager extends ArrayList<GameUpload> {

    /**
     * Adds the {@code gameUpload} to the {@code GameUploadManager} and calls
     * {@link GameUpload#setManager(GameUploadManager)}.
     *
     * @param gameUpload {@link GameUpload} to add
     * @return <b>true</b> as in {@link ArrayList#add(Object)}
     * @since 1.0
     */
    @Override
    public boolean add(GameUpload gameUpload) {
        gameUpload.setManager(this);
        return super.add(gameUpload);
    }

    /**
     * @param game {@link Game} to look for its {@code GameDownload}
     * @return {@code null} if no {@code GameDownload} of the {@code game} can be found
     * @since 1.0
     */
    public GameUpload getUpload(User user, Game game){
        for(GameUpload upload : this){
            if(upload.getUser().equals(user) && upload.getGame().equals(game))
                return upload;
        }
        return null;
    }

    /**
     * Stops the upload of all running {@link GameUpload}.
     *
     * @since 1.0
     */
    public void stopAll(){
        for(GameUpload upload : this)
            upload.stopUpload();
    }

}
