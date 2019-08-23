package stages;

import controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public abstract class Stage extends javafx.stage.Stage {
    private final static String ICON = "icon.png";

    private Controller controller;


    public Stage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(getFXML()));
        try {
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
    }

    public abstract Logger getLogger();
    public abstract String getFXML();

    public Controller getController(){
        return controller;
    }

}
