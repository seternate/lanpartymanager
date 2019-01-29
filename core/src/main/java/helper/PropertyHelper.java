package helper;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public abstract class PropertyHelper {

    public static Properties getProperty(String propertyFile) throws IOException {
        URL url = ClassLoader.getSystemResource(propertyFile);
        File fileSetting = new File(url.getPath());
        if(!fileSetting.isFile())
            throw new IOException();

        InputStream istream = new FileInputStream(fileSetting);
        Properties property = new Properties();
        property.load(istream);
        istream.close();
        return property;
    }




    /*private final static String SETTINGS_PROPERTIES = "settings/settings.properties";


    public static Properties getProperties(String path){
        Properties properties = new Properties();
        InputStream pFile = ClassLoader.getSystemResourceAsStream(path);
        if(pFile == null) {
            System.out.println("here");
            return null;

        }
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
        URL url = Thread.currentThread().getContextClassLoader().getResource(SETTINGS_PROPERTIES);
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
    */

}
