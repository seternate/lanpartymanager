package helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public abstract class PropertiesHelper {
    private final static String SETTINGS_PROPERTIES = "settings.properties";


    public static Properties getProperties(String path){
        Properties properties = new Properties();
        InputStream pFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if(pFile == null)
            return null;
        try {
            properties.load(pFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static int getServerUdp(){
        return Integer.valueOf(getSettings().getProperty("serverudp"));
    }

    public static int getServerTcp(){
        return Integer.valueOf(getSettings().getProperty("servertcp"));
    }

    public static boolean setGamePath(String gamepath){
        return setSettingsProperty("gamepath", gamepath);
    }

    public static boolean setUserName(String username){
        return setSettingsProperty("username", username);
    }

    public static String getGamepath(){
        return getSettings().getProperty("gamepath");
    }

    public static String getUsername() {
        return getSettings().getProperty("username");
    }

    private static Properties getSettings(){
        return getProperties(SETTINGS_PROPERTIES);
    }

    private static boolean setSettingsProperty(String key, String value){
        Properties properties = getSettings();
        if(properties == null)
            return false;
        properties.setProperty(key, value);
        URL url = Thread.currentThread().getContextClassLoader().getResource("../resources/" + SETTINGS_PROPERTIES);
        if(url == null)
            return false;
        FileOutputStream output;
        try {
            output = new FileOutputStream(url.getFile());
            properties.store(output, "");
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
