package entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public abstract class Settings extends Properties {
    static final String SETTINGS = "settings/settings.properties";

    public Settings() throws IOException{
        URL url = ClassLoader.getSystemResource(SETTINGS);
        File fileSetting = new File(url.getPath());
        if(!fileSetting.isFile())
            throw new IOException();

        InputStream istream = new FileInputStream(fileSetting);
        load(istream);
        istream.close();
    }

    public int getServerUdp(){
        return Integer.valueOf(getProperty("serverudp"));
    }

    public int getServerTcp(){
        return Integer.valueOf(getProperty("servertcp"));
    }
}
