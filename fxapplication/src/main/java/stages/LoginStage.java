package stages;

import controller.ApplicationManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * LoginStage class for the login window at the start and for the settings window accessed by the main stage.
 */
public class LoginStage extends Stage {
    private static Logger log = Logger.getLogger(LoginStage.class);


    /**
     * Constructs the LoginStage for the specific use case.
     */
    public LoginStage(){
        super();
        //Load FXML
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("login.fxml"));
        try {
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            log.fatal("Problem loading login.fxml.", e);
            System.exit(-2);
        }
        //Load icon
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
        //Window is not resizable
        setResizable(false);
    }

}
