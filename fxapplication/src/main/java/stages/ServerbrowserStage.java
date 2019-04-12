package stages;

import controller.ApplicationManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

/**
 * Serverbrowser class for the serverbrowser window of the application.
 */
public class ServerbrowserStage extends Stage {
    private static Logger log = Logger.getLogger(ServerbrowserStage.class);


    /**
     * Constructs the ServerbrowserStage.
     */
    public ServerbrowserStage(){
        super();
        //Loading FXML
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("serverbrowser.fxml"));
        try {
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            log.fatal("Problem loading serverbrowser.fxml.", e);
            System.exit(-3);
        }
        //Loading icon
        InputStream icon = ClassLoader.getSystemResourceAsStream("icon.png");
        if (icon != null) {
            getIcons().add(new Image(icon));
        }
        //Set title with users ip-address
        setTitle("Serverbrowser");
        //MinHeight and MinWidth for the window
        setMinWidth(350);
        setMaxWidth(500);
        setMinHeight(250);

        //Windowsize recognition
        Preferences pref = Preferences.userRoot().node("ServerbrowserStage");
        double x = pref.getDouble("win_pos_x", (Screen.getPrimary().getVisualBounds().getWidth() - getWidth()) / 2);
        double y = pref.getDouble("win_pos_y", (Screen.getPrimary().getVisualBounds().getHeight() - getHeight()) / 2);
        double width = pref.getDouble("win_width", getMinWidth());
        double height = pref.getDouble("win_height", getMinHeight());
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        this.setOnCloseRequest(event -> {
            Preferences preferences = Preferences.userRoot().node("ServerbrowserStage");
            preferences.putDouble("win_pos_x", getX());
            preferences.putDouble("win_pos_y", getY());
            preferences.putDouble("win_width", getWidth());
            preferences.putDouble("win_height", getHeight());
            log.info("Saved window size and position.");
        });
    }
}
