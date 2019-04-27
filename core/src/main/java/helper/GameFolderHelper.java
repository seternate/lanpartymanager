package helper;

import entities.game.Game;
import entities.settings.ClientSettings;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class GameFolderHelper {

    public static String getAbsolutePath(String path){
        File root = null;
        try {
            root = new File(new ClientSettings(true).getGamepath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(root == null)
            return null;
        if(!root.exists())
            //noinspection ResultOfMethodCallIgnored
            root.mkdirs();
        String[] dirs = root.list();
        for(String dir : Objects.requireNonNull(dirs)){
            File child = new File(root.getAbsolutePath()+"\\"+dir+path);
            if(child.exists()) return child.getAbsolutePath();
        }
        return null;
    }

    public static String getGameFolder(String path){
        String absolutepath = getAbsolutePath(path);
        return absolutepath.subSequence(0, absolutepath.length() - path.length()).toString();
    }

    public static String getGameFolder(Game game){
        return getGameFolder(game.getExeFileRelative());
    }
}
