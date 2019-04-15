package entities.settings;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Handles the settings for the client and user.
 */
public class ClientSettings extends Settings {
    private static Logger log = Logger.getLogger(ClientSettings.class);


    /**
     * This constructor has no implementation. Only for KryoNet.
     */
    public ClientSettings(){ }

    /**
     * Load settings from the settings-file specified by SETTINGS. Changes default gamepath to match userpath.
     *
     * @param loadSettings if true loads the settings-file, else does nothing.
     * @throws IOException if the settings-file while don't exist, is no file, any problem while reading occurs or
     *                      serverudp/servertcp/username/gamepath keys are missing.
     */
    public ClientSettings(boolean loadSettings) throws IOException {
        //Load settings file
        super(loadSettings);

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
            }
        }

        //Proper gamepath formation
        if(!getGamepath().endsWith("/"))
            setGamepath(getGamepath() + "/");
    }

    /**
     * @return username of the user.
     */
    public String getUsername(){
        return getProperty("username");
    }

    /**
     * @param username new username for the user.
     */
    public void setUsername(String username) {
        setProperty("username", username);
    }

    /**
     * @return gamepath of the user.
     */
    public String getGamepath(){
        return getProperty("gamepath");
    }

    /**
     * @param gamepath new gamepath of the user.
     */
    public void setGamepath(String gamepath) {
        setProperty("gamepath", gamepath);
    }

}
