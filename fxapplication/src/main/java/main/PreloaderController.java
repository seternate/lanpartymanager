package main;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import helper.PropertiesHelper;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PreloaderController extends Preloader {

    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnFinish;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Preloader.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("Preloader.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        txtfieldUsername.setText(PropertiesHelper.getUsername());
        txtfieldGamepath.setText(PropertiesHelper.getGamepath());
        btnFinish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                primaryStage.hide();
            }
        });

        primaryStage.show();
    }

    @Override
    public void handleProgressNotification(ProgressNotification info) {
        if(info.getProgress() >= 0.2 && info.getProgress() <= 0.3){
            lblStatus.setText("Connected to Server.");
        }else if(info.getProgress() >= 0.5){
            lblStatus.setText("Received Gamelist.");
        }
    }
}
