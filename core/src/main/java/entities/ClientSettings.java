package entities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public final class ClientSettings extends Settings {

    public ClientSettings() throws IOException {
        super();
    }

    public ClientSettings(boolean checkGamepath) throws IOException {
        this();

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
    }

    public String getUsername(){
        return getProperty("username");
    }

    public void setUsername(String username) throws IOException {
        URL url = ClassLoader.getSystemResource(SETTINGS);
        OutputStream ostream = new FileOutputStream(url.getPath());
        setProperty("username", username);
        store(ostream, "");
        ostream.close();
    }

    public String getGamepath(){
        return getProperty("gamepath");
    }

    public void setGamepath(String gamepath) throws IOException {
        URL url = ClassLoader.getSystemResource(SETTINGS);
        OutputStream ostream = new FileOutputStream(url.getPath());
        setProperty("gamepath", gamepath);
        store(ostream, "");
        ostream.close();
    }
}
