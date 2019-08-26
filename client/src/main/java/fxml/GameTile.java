package fxml;

import controller.Controller;
import controller.GameOverlayController;
import entities.game.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GameTile {

    private VBox root;
    private Controller controller;

    public GameTile(Game game, ImageView gameTileImage){
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(getFXML()));
        GameOverlayController gameOverlayController = new GameOverlayController(gameTileImage, game);
        controller = gameOverlayController;
        loader.setController(gameOverlayController);
        try {
            root = loader.load();
        } catch (IOException e) {
            getLogger().fatal("Could not loaded " + getFXML());
            getLogger().debug("Could not loaded " + getFXML(), e);
        }
    }

    public VBox getGameTile(){
        return root;
    }

    public Logger getLogger() {
        return Logger.getLogger(GameTile.class);
    }


    public String getFXML() {
        return "gameoverlay.fxml";
    }

    public Controller getController(){
        return controller;
    }

}
