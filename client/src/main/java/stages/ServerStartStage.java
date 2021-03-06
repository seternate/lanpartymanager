package stages;

import controller.Controller;
import controller.ServerStartController;
import entities.game.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

public class ServerStartStage extends javafx.stage.Stage {
    private final static String ICON = "icon.png";

    private Controller controller;


    public ServerStartStage(Game game){
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(getFXML()));
        try {
            loader.setController(new ServerStartController(game));
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            getLogger().fatal("Could not loaded " + getFXML());
            getLogger().debug("Could not loaded " + getFXML(), e);
        }
        controller = loader.getController();
        InputStream icon = ClassLoader.getSystemResourceAsStream(ICON);
        if(icon != null)
            getIcons().add(new Image(icon));
        else
            getLogger().warn("Could not load application icon.");
        setTitle("Server - Setup");
        initModality(Modality.APPLICATION_MODAL);
        setMinHeight(250);
        setMinWidth(350);

        //Windowsize recognition
        Preferences pref = Preferences.userRoot().node("ServerStartStage");
        double x = pref.getDouble("win_pos_x", (Screen.getPrimary().getVisualBounds().getWidth() - getWidth()) / 2);
        double y = pref.getDouble("win_pos_y", (Screen.getPrimary().getVisualBounds().getHeight() - getHeight()) / 2);
        double width = pref.getDouble("win_width", getMinWidth());
        double height = pref.getDouble("win_height", getMinHeight());
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        this.setOnCloseRequest(event -> {
            Preferences preferences = Preferences.userRoot().node("ServerStartStage");
            preferences.putDouble("win_pos_x", getX());
            preferences.putDouble("win_pos_y", getY());
            preferences.putDouble("win_width", getWidth());
            preferences.putDouble("win_height", getHeight());
            getLogger().info("Saved window size and position");
        });
    }

    public Logger getLogger() {
        return Logger.getLogger(ServerStartStage.class);
    }

    public String getFXML() {
        return "serverstart.fxml";
    }

    public Controller getController(){
        return controller;
    }

}
