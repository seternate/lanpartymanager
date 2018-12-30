package helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Providing easy and fast access to <code>settings.properties</code> and can serve access to all other {@link Properties}
 * files in any <code>resource</code> folder.
 * <p>
 * The {@link Properties} file <code>settings.properties</code> has the following fields:
 * <p>
 * <code>gamepath</code> - absolute path to the root game-folder holding all {@link entities.Game}.
 * <p>
 * <code>username</code> - username of the client.
 * <p>
 * <code>servertcp</code> - the port used for tcp-communication by the <code>Server</code>.
 * <p>
 * <code>serverudp</code> - the port used for udp-communication by the <code>Server</code>.
 * <p>
 * No object can be created from this class, because it only functions as a <code>helper class</code>.
 */
public abstract class PropertiesHelper {

    private final static String PROPERTIES = "settings.properties";

    /**
     * Fast access to <code>settings.properties</code>.
     *
     * @return <code>settings.properties</code>.
     */
    private static Properties getProperties(){
        return getProperties(PROPERTIES);
    }
    /**
     * Fast access to the field <code>gamepath</code> of <code>settings.properties</code>.
     *
     * @return absolute path to the root game-folder holding all {@link entities.Game}.
     */
    public static String getGamepath(){
        return getProperties().getProperty("gamepath");
    }
    /**
     * Fast access to the field <code>username</code> of <code>settings.properties</code>.
     *
     * @return username of the client.
     */
    public static String getUsername() {
        return getProperties().getProperty("username");
    }
    /**
     * Fast access to the field <code>serverudp</code> of <code>settings.properties</code>.
     *
     * @return the port used for udp-communication by the <code>Server</code>.
     */
    public static String getServerUdp(){
        return getProperties().getProperty("serverudp");
    }
    /**
     * Fast access to the field <code>servertcp</code> of <code>settings.properties</code>.
     *
     * @return the port used for tcp-communication by the <code>Server</code>.
     */
    public static String getServerTcp(){
        return getProperties().getProperty("servertcp");
    }
    /**
     * Provides fast access to any {@link Properties} files within a resource directory.
     *
     * @param path relative path of the {@link Properties} files within a resource directory.
     *
     * @return {@link Properties} file specified by <code>path</code> or a new empty {@link Properties} file.
     */
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
}
