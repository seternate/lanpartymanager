package stages;

import controller.ApplicationManager;
import controller.PreloaderController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class PreloaderStage extends Stage {
    private static Logger log = Logger.getLogger(PreloaderStage.class);


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
        setOnHiding(e -> ((PreloaderController)loader.getController()).shutdown());
    }

    @Override
    public void hide() {
        super.hide();
        if(!ApplicationManager.isRunning()){
            log.fatal("Illegal close of the PreloaderStage.");
            System.exit(-5);
        }
    }

}
