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
                System.out.println("here");
            }
        });
        lvUser.setItems(users);
        lvUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                lvGames.setItems(ApplicationManager.getUserRunServers().get(newValue));
            }
        });
    }

}
