package main;

import controller.PreloaderStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class LanFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new PreloaderStage().show();
    }
}
