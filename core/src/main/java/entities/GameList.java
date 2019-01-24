package entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GameList extends ArrayList<Game> {

    public GameList(String propertyDirectory) throws IOException {
        super();

        URL url = ClassLoader.getSystemResource(propertyDirectory);
        File filePropertyDir = new File(url.getPath());
        File[] fileProperties = filePropertyDir.listFiles();
        if(fileProperties == null)
            throw new IOException("No files in the server properties directory.");

        for(File fileProperty : fileProperties){
            if(fileProperty.getName().equals("dummy.properties") || !fileProperty.getName().endsWith(".properties"))
                continue;
            Properties property = new Properties();
            property.load(new FileInputStream(fileProperty));
            this.add(new Game(property));
        }

        if(this.isEmpty())
            throw new IOException("No properties files in the server properties directory");
    }


}
