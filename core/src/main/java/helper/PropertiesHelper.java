package helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

    public final static String RESOURCE = "../resources/settings.properties";

    public static Properties getProperties(){
        Properties properties = new Properties();
        InputStream pFile = null;
        try {
            pFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(RESOURCE);
            properties.load(pFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private PropertiesHelper(){}
}
