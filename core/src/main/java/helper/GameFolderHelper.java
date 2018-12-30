package helper;

import java.io.File;

/**
 * <code>Helper class</code> for absolute path finding of directories or files within the <code>gamepath</code> directory.
 * <p>
 * No object can be created from this class, because it only functions as a <code>helper class</code>.
 */
abstract class GameFolderHelper {

    /**
     * Searching for a file or directory in all <code>root game-folders</code> and returning the absolute path if it was
     * found anywhere.
     *
     * @param path relative path to a file or directory of a {@link entities.Game} to find. For relative path example see
     *             {@link entities.Game}.
     *
     * @return the absolute path to find from <code>path</code> or an empty String if no existing path was found.
     *
     * @see entities.Game
     */
    static String getAbsolutePath(String path){
        File root = new File(PropertiesHelper.getGamepath());
        String[] dirs = root.list();
        for(String dir : dirs){
            File child = new File(root.getAbsolutePath()+"\\"+dir+path);
            if(child.exists()) return child.getAbsolutePath();
        }
        return "";
    }
}
