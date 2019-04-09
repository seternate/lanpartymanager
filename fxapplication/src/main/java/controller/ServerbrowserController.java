package controller;

import entities.game.Game;
import entities.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ServerbrowserController {
    ObservableList<User> users = FXCollections.observableArrayList(ApplicationManager.getUserRunServers().keySet());
    @FXML
    private ListView<User> lvUser;
    @FXML
    private ListView<Game> lvGames;



    @FXML
    private void initialize(){
        ApplicationManager.getUserRunServers().addListener(new MapChangeListener<User, ObservableList<Game>>() {
            @Override
            public void onChanged(Change<? extends User, ? extends ObservableList<Game>> change) {
                users.setAll(ApplicationManager.getUserRunServers().keySet());
            }
        });
        lvUser.setItems(users);
        lvUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                lvGames.setItems(ApplicationManager.getUserRunServers().get(newValue));
            }
        });
        lvGames.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                Game game = lvGames.getSelectionModel().getSelectedItem();
                User user = lvUser.getSelectionModel().getSelectedItem();
                ApplicationManager.connectServer(game, user.getIpAddress());
            }
        });
    }

}
