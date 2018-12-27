package helper;

import java.io.File;

public class GameFolderHelper {

    public static String getRootFolder(String path){
        File root = new File(PropertiesHelper.getProperties().getProperty("gamepath"));
        String[] dirs = root.list();
        for(String dir : dirs){
            System.out.println(dir + " : " + root.getAbsolutePath()+"\\"+dir+path);
            File child = new File(root.getAbsolutePath()+"\\"+dir+path);
            if(child.exists()) return child.getAbsolutePath();
        }
        return "";
    }

    private GameFolderHelper(){}
}
