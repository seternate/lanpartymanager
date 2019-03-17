package stages;

import controller.ApplicationManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * LoginStage class for the login window at the start and for the settings window accessed by the main stage.
 */
public class LoginStage extends Stage {
    public LoginStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("login.fxml"));
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
        /*
            Changing stage title based on the opening.
            If entered from startup the login window opened, entered from main stage the settings window opened.
         */
        if(!ApplicationManager.isMainstage())
            setTitle("Lanpartymanager - Login");
        else
            setTitle("Settings");
        setResizable(false);
    }
}
