package controller;

import entities.game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ServerStartController extends Controller{
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
        getClient().startServer(game, txtParameters.getText(), true);
        txtParameters.getScene().getWindow().hide();
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            startServer();
    }

    @Override
    public void shutdown() {

    }
}
