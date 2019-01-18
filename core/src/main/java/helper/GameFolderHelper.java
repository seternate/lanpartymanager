package helper;

import java.io.File;
import java.util.Objects;

public abstract class GameFolderHelper {
    public static String getAbsolutePath(String path){
        File root = new File(PropertiesHelper.getGamepath());
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
}
