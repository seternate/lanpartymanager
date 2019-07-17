package controller;

import entities.game.Game;
import entities.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerConnectController {
    private Game game;
    private ObservableList<User> users;
    @FXML
    private ListView<User> lvUsers;


    public ServerConnectController(Game game){
        this.game = game;
    }

    @FXML
    private void initialize(){
        users = FXCollections.observableArrayList(getUserWithOpenServer());
        ApplicationManager.getUserRunServers().forEach((user, gamelist) -> {
            gamelist.addListener(new ListChangeListener<Game>() {
                @Override
                public void onChanged(Change<? extends Game> c) {
                    users.setAll(getUserWithOpenServer());
                }
            });
        });
        ApplicationManager.getUserRunServers().addListener(new MapChangeListener<User, ObservableList<Game>>() {
            @Override
            public void onChanged(Change<? extends User, ? extends ObservableList<Game>> change) {
                if(change.wasAdded()) {
                    change.getValueAdded().addListener(new ListChangeListener<Game>() {
                        @Override
                        public void onChanged(Change<? extends Game> c) {
                            users.setAll(getUserWithOpenServer());
                        }
                    });
                }
                users.setAll(getUserWithOpenServer());
            }
        });
        lvUsers.setItems(users);
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

    private List<User> getUserWithOpenServer(){
        List<User> userlist = new ArrayList<>();
        ApplicationManager.getUserRunServers().forEach((user, gamelist) -> {
            for(Game g : gamelist){
                if(g != null && g.equals(game))
                    userlist.add(user);
            }
        });
        return userlist;
    }

}

