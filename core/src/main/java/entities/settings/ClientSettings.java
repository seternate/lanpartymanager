package entities.settings;

import entities.user.User;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * {@code ClientSettings} manages the settings for the {@link User}.
 * <br><br>
 * <p>
 *     Example for {@code ClientSettings} propertyfile:
 *     <br>
 *     username = seternate
 *     <br>
 *     gamepath = C:/Users/seternate/Desktop/lan_games
 *     <br>
 *     servertcp = 54555 [must match the {@code ServerSettings}]
 *     <br>
 *     serverudp = 54777 [must match the {@code ServerSettings}]
 * </p>
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ClientSettings extends Settings {
    private static Logger log = Logger.getLogger(ClientSettings.class);


    /**
     * Creates the {@code ClientSettings}.
     *
     * @since 1.0
     */
    public ClientSettings(){ }

    /**
     * Creates the {@code ClientSettings}.
     * <p>
     *     Load settings from {@value SETTINGS} if {@code loadSettings} is <b>true</b>. Else no settings are loaded.
     * </p>
     *
     * @param loadSettings <b>true</b> if the settings should be loaded, else <b>false</b>
     * @throws IOException if {@code settings.properties} do not exist, any problem while reading occurs or
     *                      serverudp/servertcp/username/gamepath keys are missing
     * @since 1.0
     */
    public ClientSettings(boolean loadSettings) throws IOException {
        //Load settings file
        super(loadSettings);
        //Return if no settings should be loaded
        if(!loadSettings)
            return;
        //Check if username and gamepath key is missing
        if(getProperty("username") == null || getProperty("gamepath") == null){
            throw new NullPointerException("'username' or/and 'gamepath' key is missing in the settings file.");
        }
        //Change default gamepath to match user
        if(getGamepath().contains("C:/Users/")){
            String[] pathsplit = getGamepath().split("/");
            if(pathsplit.length >= 3 && pathsplit[2].equals("asd")){
                String winUser = System.getProperty("user.name");
                StringBuilder newPath = new StringBuilder("C:/Users/" + winUser + "/");
                for(int i = 3; i < pathsplit.length; i++){
                    newPath.append(pathsplit[i]).append("/");
                }
                log.info("Changed default gamepath '" + getGamepath() + "' to '" + newPath.toString() + "'.");
                setGamepath(newPath.toString());
                save();
            }
        }
        //Proper gamepath formation
        if(!getGamepath().endsWith("/")) {
            setGamepath(getGamepath() + "/");
            save();
        }
    }

    /**
     * @return username of the {@code User}
     * @since 1.0
     */
    public String getUsername(){
        return getProperty("username");
    }

    /**
     * @param username username for the {@code User}
     * @since 1.0
     */
    public void setUsername(String username) {
        setProperty("username", username);
    }

    /**
     * @return gamepath of the {@code User}
     * @since 1.0
     */
    public String getGamepath(){
        return getProperty("gamepath");
    }

    /**
     * @param gamepath gamepath of the {@code User}
     * @since 1.0
     */
    public void setGamepath(String gamepath) {
        StringBuilder path = new StringBuilder(gamepath);
        if(!path.toString().endsWith("/")) {
            path.append("/");
        }
        setProperty("gamepath", path.toString());
    }

}
