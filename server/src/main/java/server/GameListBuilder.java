package server;

import entities.Game;
import helper.PropertiesHelper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;

public class GameListBuilder {

    public static ArrayList<Game> build(){
        ArrayList<Game> gameList = new ArrayList<>();
        Properties[] properties = getGameproperties();
        for(Properties property : properties){
            gameList.add(new Game(property));
        }
        return gameList;
    }

    private static Properties[] getGameproperties(){
        URI uri = null;
        try {
            uri = Thread.currentThread().getContextClassLoader().getResource("../resources/").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File resources = new File(uri);
        String[] list = resources.list();
        Properties[] properties = new Properties[list.length];
        for(int i = 0; i< list.length; i++){
            properties[i] = PropertiesHelper.getProperties(list[i]);
        }
        return properties;
    }

    private GameListBuilder(){}
}
