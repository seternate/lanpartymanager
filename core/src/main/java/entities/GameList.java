package entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public final class GameList extends ArrayList<Game> {

    public GameList(){ }

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
            InputStream istream = new FileInputStream(fileProperty);
            property.load(istream);
            istream.close();
            this.add(new Game(property));
        }

        if(this.isEmpty())
            throw new IOException("No properties files in the server properties directory");
    }

    public boolean equals(GameList gamelist){
        if(size() != gamelist.size())
            return false;
        for(Game game : this){
            for(int k = 0; k < gamelist.size(); k++){
                if(!game.equals(gamelist.get(k)) && k == gamelist.size() - 1){
                    return false;
                }else if(game.equals(gamelist.get(k))){
                    break;
                }
            }
        }
        return true;
    }

}
