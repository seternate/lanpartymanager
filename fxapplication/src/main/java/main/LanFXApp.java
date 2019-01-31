package main;

import controller.ApplicationManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class LanFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ApplicationManager.start();
    }
}
