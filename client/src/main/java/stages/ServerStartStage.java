package stages;

import controller.ServerStartController;
import entities.game.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

public class ServerStartStage extends Stage {
    private static Logger log = Logger.getLogger(ServerStartStage.class);


    public ServerStartStage(Game game){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("serverstart.fxml"));
        try {
            loader.setController(new ServerStartController(game));
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream icon = ClassLoader.getSystemResourceAsStream("icon.png");
        if (icon != null) {
            getIcons().add(new Image(icon));
        }
        setTitle("Server - Setup");
        initModality(Modality.APPLICATION_MODAL);
        setMinHeight(250);
        setMinWidth(300);

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
            log.info("Saved window size and position");
        });
    }

}
