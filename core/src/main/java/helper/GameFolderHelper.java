package helper;

import entities.game.Game;
import entities.settings.ClientSettings;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * {@code GameFolderHelper} is a helper class to get the absolute path of a relative path within a gamefolder. Also
 * the gamefolder of an relative path within a gamefolder can be determined.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public abstract class GameFolderHelper {

    /**
     * Returns the absolute path of the {@code path}. {@code path} is given as a relative path within a gamefolder.
     *
     * @param path relative path within a gamefolder
     * @return absolute path of the {@code path}
     * @since 1.0
     */
    public static String getAbsolutePath(String path){
        File root = null;
        //Open gamepathfolder
        try {
            root = new File(new ClientSettings(true).getGamepath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(root == null)
            return null;
        //Create the gamepathfolder if it does not exist
        if(!root.exists() && !root.mkdirs())
            return null;
        //List all folder/files in the gamepathfolder
        String[] dirs = root.list();
        if(dirs == null || dirs.length == 0)
            return null;
        //Find the right gamefolder, which belongs to the relative path
        for(String dir : dirs){
            File child = new File(root.getAbsolutePath()+"\\"+dir+path);
            if(child.exists())
                return child.getAbsolutePath();
        }
        return null;
    }

    /**
     * Returns the absolute path of the gamefolder holding the relative path {@code path}.
     *
     * @param path relative path within a gamefolder
     * @return absolute path of the gamefolder
     * @since 1.0
     */
    public static String getGameFolder(String path){
        String absolutepath = getAbsolutePath(path);
        return absolutepath.subSequence(0, absolutepath.length() - path.length()).toString();
    }

    /**
     * Returns the absolute path of the gamefolder from the {@code Game}.
     *
     * @param game {@link Game} to get the gamefolder
     * @return absolute path of the gamefolder from the {@code game}
     * @since 1.0
     */
    public static String getGameFolder(Game game){
        return getGameFolder(game.getExeFileRelative());
    }

}
