package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;

    @FXML
    private void initialize(){
        txtfieldUsername.setText(ApplicationManager.getUsername());
        txtfieldGamepath.setText(ApplicationManager.getGamepath());
        ApplicationManager.setServerStatusLabel(lblStatus);
    }

    @FXML
    private void openMainStage(){
        if(ApplicationManager.isConnected()){
            ApplicationManager.openMainStage(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim());
        }
    }

    @FXML
    private void enter(){
        openMainStage();
    }
}
