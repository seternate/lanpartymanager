package entities.settings;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * {@code Settings} loading and managing any settings within a {@code propertyfile}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public abstract class Settings extends Properties {
    public static final String SETTINGS = "settings/settings.properties";

    /**
     * Creates the {@code Settings}.
     *
     * @since 1.0
     */
    public Settings(){ }

    /**
     * Creates the {@code Settings}.
     * <p>
     *     Load settings from {@value SETTINGS} if {@code loadSettings} is <b>true</b>. Else no settings are loaded.
     * </p>
     *
     * @param loadSettings <b>true</b> if the settings should be loaded, else <b>false</b>
     * @throws IOException if {@code settings.properties} do not exist, any problem while reading occurs or
     *                     serverudp/servertcp/username/gamepath keys are missing
     * @since 1.0
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
     * @return UDP-Port of the {@code LANServer}
     * @since 1.0
     */
    public int getServerUdp(){
        return Integer.valueOf(getProperty("serverudp"));
    }

    /**
     * @return TCP-Port of the {@code LANServer}
     * @since 1.0
     */
    public int getServerTcp(){
        return Integer.valueOf(getProperty("servertcp"));
    }

    /**
     * Saves the settings to {@value SETTINGS}.
     *
     * @throws IOException if any error while saving the properties occurs
     * @since 1.0
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
