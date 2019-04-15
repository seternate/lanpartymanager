package entities.settings;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Load settings from a properties file.
 */
public abstract class Settings extends Properties {
    public static final String SETTINGS = "settings/settings.properties";

    /**
     * This constructor has no implementation. Only for KryoNet.
     */
    public Settings(){ }

    /**
     * Load settings from the settings-file specified by SETTINGS.
     *
     * @param loadSettings if true loads the settings-file, else does nothing.
     * @throws IOException if the settings-file while don't exist, is no file, any problem while reading occurs or
     *                      serverudp/servertcp keys are missing.
     */
    public Settings(boolean loadSettings) throws IOException {
        super();
        if(!loadSettings)
            return;
        //Get settings-file specified by SETTINGS
        URL url = ClassLoader.getSystemResource(SETTINGS);
        if(url == null){
            throw new FileNotFoundException("Resource not found.");
        }
        //Decode URL-encoded path
        File settingsFile = new File(java.net.URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8.name()));
        if(!settingsFile.isFile()){
            throw new FileNotFoundException("Resource specified by SETTINGS is no file.");
        }

        //Read settings properties
        InputStream istream = new FileInputStream(settingsFile);
        load(istream);
        istream.close();

        //Check settings file for udp and tcp key
        if(getProperty("serverudp") == null || getProperty("servertcp") == null){
            throw new NullPointerException("'serverudp' or/and 'servertcp' key is missing in the settings file.");
        }
    }

    /**
     * @return udp-port of the server.
     */
    public int getServerUdp(){
        return Integer.valueOf(getProperty("serverudp"));
    }

    /**
     * @return tcp-port of the server.
     */
    public int getServerTcp(){
        return Integer.valueOf(getProperty("servertcp"));
    }

    /**
     * Saves all settings to the file specified in SETTINGS.
     *
     * @throws IOException if any error occurs while saving to the properties file.
     */
    public void save() throws IOException {
        //Get settings-file specified by SETTINGS
        URL url = ClassLoader.getSystemResource(SETTINGS);
        //Decode URL-encoded path
        OutputStream ostream = new FileOutputStream(java.net.URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8.name()));
        store(ostream, "");
        ostream.close();
    }
}
