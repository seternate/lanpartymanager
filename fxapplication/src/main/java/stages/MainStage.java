package stages;

import controller.MainController;
import entities.GameList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class MainStage extends Stage {
    private MainController controller;

    public MainStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("main.fxml"));
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
        setTitle("Lanpartymanager");
        controller = loader.getController();
        setMinWidth(640);
        setMinHeight(480);
    }

    public void updateRoot(){
        //controller.updateRoot();
    }

}
