package server;

import entities.Game;
import helper.PropertiesHelper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

/**
 * Builds the <code>game-list</code> for the {@link Server}. Only games with a {@link Properties} file are created.
 * A new game is added by adding the game-folder of the game to the specified folder in <code>settings.properties</code>
 * and by adding a new {@link Properties} file in the <code>resource</code> folder.
 * <p>
 * No object can be created from this class, because it only functions as a <code>helper class</code> for the {@link Server}.
 *
 * @see Game
 */
abstract class GameListBuilder {

    /**
     * Builds the <code>game-list</code> from all {@link Properties} files in the <code>resource</code> folder.
     *
     * @return {@link ArrayList} of all games provided from the {@link Server}.
     */
    static ArrayList<Game> build(){
        ArrayList<Game> gameList = new ArrayList<>();
        Properties[] properties = getGameproperties();
        for(Properties property : properties){
            gameList.add(new Game(property));
        }
        return gameList;
    }
    /**
     * All {@link Properties} from the games in the <code>resource</code> folder.
     *
     * @return <code>Array</code> of all {@link Properties} files from the games in the <code>resource</code> folder.
     */
    private static Properties[] getGameproperties(){
        //Get the URI of the resource-folder
        URI uri = null;
        try {
            uri = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("../resources/")).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (uri == null) throw new AssertionError();

        //Create a list of all properties-files in the resource-folder
        File resources = new File(uri);
        String[] list = resources.list();
        if (list == null) throw new AssertionError();
        Properties[] properties = new Properties[list.length];

        //Load all properties-files and return them
        for(int i = 0; i < list.length; i++){
            properties[i] = PropertiesHelper.getProperties(list[i]);
        }
        return properties;
    }
}
