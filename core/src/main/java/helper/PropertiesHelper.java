package helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

    public final static String PROPERTIES = "settings.properties";

    public static Properties getProperties(){
        return getProperties(PROPERTIES);
    }

    public static String getGamepath(){
        return getProperties().getProperty("gamepath");
    }

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

    private PropertiesHelper(){}
}
