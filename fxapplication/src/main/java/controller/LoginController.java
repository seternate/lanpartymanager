package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controller class of the login and settings stage.
 */
public class LoginController {
    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnFinish;

    @FXML
    private void initialize(){
        txtfieldUsername.setText(ApplicationManager.getUsername());
        txtfieldGamepath.setText(ApplicationManager.getGamepath());
        if(!ApplicationManager.isMainstage())
            ApplicationManager.setServerStatusLabel(lblStatus);
        else {
            lblStatus.setText("");
            btnFinish.setText("Save");
        }
    }

    @FXML
    private void openMainStage(){
        if(ApplicationManager.getServerStatus() != null && ApplicationManager.isConnected()
                && !ApplicationManager.isMainstage())
            ApplicationManager.openMainStage(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim());
        else if(ApplicationManager.getServerStatus() != null && ApplicationManager.isConnected()
                && ApplicationManager.isMainstage())
            ApplicationManager.saveSettings(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim());
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            openMainStage();
    }

}
