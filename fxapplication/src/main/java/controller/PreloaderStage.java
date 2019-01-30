package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PreloaderStage extends Stage {

    public PreloaderStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("preloader.fxml"));
        Parent rootNode = null;
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setScene(new Scene(rootNode));
        initStyle(StageStyle.UNDECORATED);
    }

}
