package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;
import stages.MainStage;

/**
 * Controller class of the login and settings stage.
 */
public class LoginController {
    private static Logger log = Logger.getLogger(LoginController.class);

    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnFinish;


    /**
     * Initializing the LoginController.
     */
    @FXML
    private void initialize(){
        log.info("Initializing.");
        //Setting the text for the username and gamepath textfields
        txtfieldUsername.setText(ApplicationManager.getUsername());
        txtfieldGamepath.setText(ApplicationManager.getGamepath());
        if(!ApplicationManager.isMainstage()) {
            ApplicationManager.setServerStatusLabel(lblStatus);
            log.info("Setting up 'LoginStage'.");
        } else {
            lblStatus.setText("");
            btnFinish.setText("Save");
            log.info("Setting up 'SettingStage'.");
        }
    }

    /**
     * Button click listener.
     */
    @FXML
    private void openMainStage(){
        //Opens MainStage if Stage is as LoginStage open
        if(ApplicationManager.getServerStatus() != null && ApplicationManager.isConnected()
                && !ApplicationManager.isMainstage())
            ApplicationManager.openMainStage(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim());
        //Saves the settings if the Stage is as SettingsStage open
        else if(ApplicationManager.getServerStatus() != null && ApplicationManager.isConnected()
                && ApplicationManager.isMainstage())
            ApplicationManager.saveSettings(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim());
    }

    /**
     * KeyPressed event listener.
     *
     * @param event KeyEvent that was fired.
     */
    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            openMainStage();
    }

}
