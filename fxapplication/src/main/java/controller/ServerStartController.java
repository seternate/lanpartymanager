package controller;

import entities.Game;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ServerStartController {
    private Game game;

    @FXML
    private TextArea txtParameters;


    public ServerStartController(Game game){
        this.game = game;
    }

    @FXML
    private void initialize(){
        txtParameters.setText(game.getServerParam());
    }

    @FXML
    private void startServer(){
        ApplicationManager.startServer(game, txtParameters.getText());
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            startServer();
    }
}
