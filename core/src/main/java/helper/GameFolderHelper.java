package helper;

import entities.ClientSettings;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class GameFolderHelper {
    public static String getAbsolutePath(String path){
        File root = null;
        try {
            root = new File(new ClientSettings(true, true).getGamepath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(root == null)
            return null;
        if(!root.exists())
            //noinspection ResultOfMethodCallIgnored
            root.mkdirs();
        System.out.println(root.getAbsolutePath());
        String[] dirs = root.list();
        for(String dir : Objects.requireNonNull(dirs)){
            File child = new File(root.getAbsolutePath()+"\\"+dir+path);
            if(child.exists()) return child.getAbsolutePath();
        }
        return null;
    }
}
