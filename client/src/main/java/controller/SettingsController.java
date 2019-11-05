package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SettingsController extends Controller{

    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;


    @FXML
    private void initialize(){
        txtfieldUsername.setText(getClient().getUser().getUsername());
        txtfieldGamepath.setText(getClient().getUser().getGamepath());
    }

    @FXML
    private void save(){
        if(getClient().updateUser(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim()))
            txtfieldGamepath.getScene().getWindow().hide();
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            save();
        }
    }

    @Override
    public void shutdown() {

    }

}
