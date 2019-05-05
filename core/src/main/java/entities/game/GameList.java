package entities.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * {@code GameList} handles {@link Game}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class GameList extends ArrayList<Game> {

    /**
     * Creates the {@code GameList}.
     *
     * @since 1.0
     */
    public GameList(){ }

    /**
     * Creates the {@code GameList} of all {@link Game} with a propertyfile within the {@code gamePropertyDirectory}.
     *
     * @param gamePropertyDirectory relative path of the directory with all propertyfiles to be loaded
     * @throws IOException if no properties files are found in the gamePropertyDirectory or any problem occurs while
     *                      loading the propertyfiles
     * @since 1.0
     */
    public GameList(String gamePropertyDirectory) throws IOException {
        super();
        //Get properties files directory
        URL url = ClassLoader.getSystemResource(gamePropertyDirectory);
        //Decode URL-encoded path
        File filePropertyDir = new File(java.net.URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8.name()));
        //List all available files in the directory
        File[] fileProperties = filePropertyDir.listFiles();
        //Check if there are any files in the directory, else throw exception
        if(fileProperties == null)
            throw new IOException("No files in the server properties directory.");
        //Iterate through all files in directory and load all properties files to a list
        for(File fileProperty : fileProperties){
            //All properties files should be add, except 'dummy'
            if(fileProperty.getName().equals("dummy.properties") || !fileProperty.getName().endsWith(".properties"))
                continue;
            //Load properties
            Properties property = new Properties();
            InputStream istream = new FileInputStream(fileProperty);
            property.load(istream);
            istream.close();
            //Add properties to list
            this.add(new Game(property));
        }
        //Throw a new exception if no properties files where found
        if(this.isEmpty())
            throw new IOException("No properties files in the server properties directory.");
    }

    /**
     * Compares the {@link Game} of this {@code GameList} with the {@code Game} of {@code gamelist}.
     *
     * @param gamelist {@link GameList} to be compared
     * @return <b>true</b> if this {@code GameList} and {@code gamelist} have the same {@code Game}, else <b>false</b>
     */
    public boolean equals(GameList gamelist){
        //Return false if the size differs
        if(size() != gamelist.size())
            return false;
        //Iterate through all elements of this gamelist
        for(Game game : this){
            //Iterate through all elements of gamelist
            for(int k = 0; k < gamelist.size(); k++){
                //Return false if reached the end of gamelist without find any equal game, else check next game
                //of this object until through this gamelist
                if(!game.equals(gamelist.get(k)) && k == gamelist.size() - 1)
                    return false;
                else if(game.equals(gamelist.get(k)))
                    break;
            }
        }
        return true;
    }

}
