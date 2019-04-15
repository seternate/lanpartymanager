package controller;

import entities.game.Game;
import entities.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ServerConnectController {
    private Game game;

    @FXML
    private ListView<User> lvUsers;

    public ServerConnectController(Game game){
        this.game = game;
    }

    @FXML
    private void initialize(){
        ObservableList<User> userlist = FXCollections.observableArrayList(ApplicationManager.getUserslist());
        FXCollections.copy(userlist, ApplicationManager.getUserslist());
        userlist.add(ApplicationManager.getUser());
        lvUsers.setItems(userlist);
        lvUsers.setCellFactory(c -> new ListCell<User>(){
            @Override
            protected void updateItem(User item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                if(item != null){
                    setText(item.getUsername());
                }
            }
        });
        lvUsers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                    ApplicationManager.connectServer(game, lvUsers.getSelectionModel().getSelectedItem().getIpAddress());
                }
            }
        });
    }
}
