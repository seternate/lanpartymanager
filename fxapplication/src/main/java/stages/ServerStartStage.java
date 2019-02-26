package stages;

import controller.ServerStartController;
import entities.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class ServerStartStage extends Stage {

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
    }
}
