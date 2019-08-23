package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

public class LoginController extends Controller{
    private static Logger log = Logger.getLogger(LoginController.class);

    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnFinish;


    @FXML
    private void initialize(){
        log.info("Initializing.");
        //Setting the text for the username and gamepath textfields
        txtfieldUsername.setText(ApplicationManager.getUsername());
        txtfieldGamepath.setText(ApplicationManager.getGamepath());
        ApplicationManager.setServerStatusLabel(lblStatus);
        log.info("Setting up 'LoginStage'.");
    }

    @FXML
    private void openMainStage(){
        log.info("Open MainStage.");
        ApplicationManager.openMainStage(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim());
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER) {
            log.info("'Enter' was pressed.");
            openMainStage();
        }
    }

    @Override
    public void shutdown() {

    }
}
