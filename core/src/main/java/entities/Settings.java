package entities;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public abstract class Settings extends Properties {
    static final String SETTINGS = "settings/settings.properties";

    public Settings(){ }

    public Settings(boolean loadSettings) throws IOException {
        if(!loadSettings)
            return;
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

    public void save() throws IOException {
        URL url = ClassLoader.getSystemResource(SETTINGS);
        OutputStream ostream = new FileOutputStream(url.getPath());
        store(ostream, "");
        ostream.close();
    }
}
