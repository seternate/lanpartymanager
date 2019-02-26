package stages;

import controller.ServerConnectController;
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

public class ServerConnectStage extends Stage {

    public ServerConnectStage(Game game){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("serverconnect.fxml"));
        try {
            loader.setController(new ServerConnectController(game));
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream icon = ClassLoader.getSystemResourceAsStream("icon.png");
        if (icon != null) {
            getIcons().add(new Image(icon));
        }
        setTitle("Server - Connect");
        initModality(Modality.APPLICATION_MODAL);
    }
}
