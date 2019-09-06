package controller;

import entities.game.Game;
import entities.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


public class NotificationController extends Controller {

    @FXML
    private Label lblHead, lblText;
    @FXML
    private ImageView ivImage;
    private String head, text;
    private Game game;
    private User user;

    public NotificationController(User user, Game game){
        head = game.getName();
        text = user.getUsername() + " opened a new server.";
        this.game = game;
        this.user = user;
    }

    @FXML
    private void initialize(){
        lblHead.setText(head);
        lblText.setText(text);
        ivImage.setImage(ControllerHelper.getIcon(game));
    }

    @Override
    public void shutdown() {

    }

    @FXML
    public void mouseClick(MouseEvent event){
        if(event.getButton() == MouseButton.PRIMARY && getClient().getUserRunServer().get(user) != null && getClient().getUserRunServer().get(user).contains(game)){
            getClient().connectServer(game, user.getIpAddress(), true);
        }
    }

}
