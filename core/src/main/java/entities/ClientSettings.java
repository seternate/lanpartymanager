package entities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class ClientSettings extends Settings {

    public ClientSettings() throws IOException {
        super();
    }

    public String getUsername(){
        return getProperty("username");
    }

    public String getGamepath(){
        return getProperty("gamepath");
    }

    public void setUsername(String username) throws IOException {
        URL url = ClassLoader.getSystemResource(Settings.SETTINGS);
        OutputStream ostream = new FileOutputStream(url.getPath());
        setProperty("username", username);
        store(ostream, "");
        ostream.close();
    }
}
