package helper;

import java.io.File;

public class GameFolderHelper {

    public static String getAbsolutePath(String path){
        File root = new File(PropertiesHelper.getGamepath());
        String[] dirs = root.list();
        for(String dir : dirs){
            File child = new File(root.getAbsolutePath()+"\\"+dir+path);
            if(child.exists()) return child.getAbsolutePath();
        }
        return "";
    }

    private GameFolderHelper(){}
}
