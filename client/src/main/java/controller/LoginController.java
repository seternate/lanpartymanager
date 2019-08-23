package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController extends Controller{

    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;
    private ChangeListener<Boolean> statusListener;


    @FXML
    private void initialize(){
        txtfieldUsername.setText(getClient().getUser().getUsername());
        txtfieldGamepath.setText(getClient().getUser().getGamepath());
        if(getClient().getStatus().isConnected())
            lblStatus.setText("Connected to server: " + getClient().getStatus().getServerIP());
        else
            lblStatus.setText("Waiting for server connection.");
        statusListener = (observable, oldValue, newValue) -> {
            if(newValue)
                Platform.runLater(() -> lblStatus.setText("Connected to server: " + getClient().getStatus().getServerIP()));
            else
                Platform.runLater(() -> lblStatus.setText("Waiting for server connection."));
        };
        getClient().getStatus().getConnectedProperty().addListener(statusListener);
    }

    @FXML
    private void openMainStage(){
        String username = txtfieldUsername.getText().trim();
        String gamepath = txtfieldGamepath.getText().trim();
        ApplicationManager.openMainStage(username, gamepath);
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            openMainStage();
    }

    @Override
    public void shutdown() {
        getClient().getStatus().getConnectedProperty().removeListener(statusListener);
    }

}
