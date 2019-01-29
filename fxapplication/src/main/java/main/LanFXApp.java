package main;

import controller.ApplicationManager;
import controller.LoginStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class LanFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LoginStage stage = new LoginStage();
        stage.show();
        //ApplicationManager manager = new ApplicationManager(primaryStage);
        //manager.loadLogin();
    }
}
