package main;

import clientInterface.Client;
import controller.ApplicationManager;
import javafx.application.Application;
import javafx.stage.Stage;
import stages.PreloaderStage;

public class LanFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ApplicationManager.startApplication();
    }
}
