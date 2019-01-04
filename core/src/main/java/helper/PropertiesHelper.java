package helper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertiesHelper {
    private final static String SETTINGS_PROPERTIES = "settings.properties";

    public static Properties getProperties(String path){
        Properties properties = new Properties();
        InputStream pFile = null;
        try {
            pFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("../resources/" + path);
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

    private static Properties getSettings(){
        return getProperties(SETTINGS_PROPERTIES);
    }







    private static void setProperty(String key, String value){
        Properties properties = getSettings();
        properties.setProperty(key, value);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource("../resources/" + SETTINGS_PROPERTIES).getFile());
            properties.store(output, "");
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getGamepath(){
        return getSettings().getProperty("gamepath");
    }

    public static void setGamePath(String gamepath){
        setProperty("gamepath", gamepath);
    }

    public static String getUsername() {
        return getSettings().getProperty("username");
    }

    public static void setUserName(String username){
        setProperty("username", username);
    }






}
