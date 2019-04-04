package stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * PreloaderStage for the preloading splashscreen.
 */
public class PreloaderStage extends Stage {
    private static Logger log = Logger.getLogger(PreloaderStage.class);


    /**
     * Constructs the PreloaderStage.
     */
    public PreloaderStage(){
        super();
        //Load FXML
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("preloader.fxml"));
        try {
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            log.fatal("Problem loading preloader.fxml.", e);
            System.exit(-1);
        }
        //Load icon
        InputStream icon = ClassLoader.getSystemResourceAsStream("icon.png");
        if (icon != null) {
            getIcons().add(new Image(icon));
        }
        //Change style to borderless undecorated
        initStyle(StageStyle.UNDECORATED);
        //Window is not resizable
        setResizable(false);
        //Set title
        setTitle("Lanpartymanager - Splash");
    }

}
