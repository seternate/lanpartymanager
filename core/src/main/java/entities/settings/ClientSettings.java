package entities.settings;

import java.io.IOException;

public final class ClientSettings extends Settings {

    public ClientSettings(){ }

    public ClientSettings(boolean checkGamepath, boolean loadSettings) throws IOException {
        super(loadSettings);

        if(getGamepath().contains("C:/Users/") && checkGamepath){
            String[] splitted = getGamepath().split("/");
            String winUser = System.getProperty("user.name");
            if(splitted.length >= 3 && !splitted[2].equals(winUser)){
                StringBuilder newPath = new StringBuilder("C:/Users/" + winUser + "/");
                for(int i = 3; i < splitted.length; i++){
                    newPath.append(splitted[i]).append("/");
                }
                setGamepath(newPath.toString());
            }
        }

        if(checkGamepath){
            if(!getGamepath().endsWith("/"))
                setGamepath(getGamepath() + "/");
        }
    }

    public String getUsername(){
        return getProperty("username");
    }

    public void setUsername(String username) {
        setProperty("username", username);
    }

    public String getGamepath(){
        return getProperty("gamepath");
    }

    public void setGamepath(String gamepath) {
        setProperty("gamepath", gamepath);
    }

}
