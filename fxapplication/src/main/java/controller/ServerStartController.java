package controller;

import entities.Game;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

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
}
