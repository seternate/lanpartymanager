package entities;

import java.io.IOException;

public final class ServerSettings extends Settings {

    public ServerSettings() throws IOException {
        super(true);
        if(getProperty("serverudp") == null && getProperty("servertcp") == null)
            throw new IOException();
    }
}
