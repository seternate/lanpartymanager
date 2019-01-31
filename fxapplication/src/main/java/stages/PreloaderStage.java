package stages;

import controller.PreloaderController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class PreloaderStage extends Stage {
    private PreloaderController controller;

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
        controller = loader.getController();
    }

    @Override
    public void hide(){
        super.hide();
        controller.stopAnimations();
    }
}
