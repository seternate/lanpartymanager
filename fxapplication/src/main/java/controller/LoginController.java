package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Button btnLogin;
    @FXML
    private Label lblStatus;

    @FXML
    private void initialize(){
        txtfieldUsername.setText(ApplicationManager.getUsername());
        txtfieldGamepath.setText(ApplicationManager.getGamepath());
    }

    @FXML
    private void openMainStage(){

    }

    @FXML
    private void enter(){

    }
}
