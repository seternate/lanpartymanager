package stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

/**
 * PreloaderStage for the preloading splashscreen.
 */
public class PreloaderStage extends Stage {
    public PreloaderStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("preloader.fxml"));
        try {
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream icon = ClassLoader.getSystemResourceAsStream("icon.png");
        if (icon != null) {
            getIcons().add(new Image(icon));
        }
        initStyle(StageStyle.UNDECORATED);
        setResizable(false);
        setTitle("Lanpartymanager - Splash");
    }
}
