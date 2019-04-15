package controller;

import entities.game.Game;
import entities.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class ServerbrowserController {

    private class GameCell extends ListCell<Game> {
        @Override
        protected void updateItem(Game item, boolean empty) {
            super.updateItem(item, empty);
            this.setGraphicTextGap(10);
            setGraphic(null);
            setText(null);
            if(item != null){
                setText(item.getName());
                Image image = ControllerHelper.getIcon(item);
                ImageView icon = new ImageView(image);
                icon.setFitHeight(32);
                icon.setFitWidth(32);
                setGraphic(icon);
            }
        }
    }


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
        MenuItem itemIPAddress = new MenuItem("Copy IP to clipboard");
        itemIPAddress.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(lvUser.getSelectionModel().getSelectedItem().getIpAddress());
                clipboard.setContent(content);
            }
        });
        MenuItem itemIP = new MenuItem("Copy IP to clipboard");
        itemIPAddress.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(lvUser.getSelectionModel().getSelectedItem().getIpAddress());
                clipboard.setContent(content);
            }
        });
        MenuItem itemJoin = new MenuItem("Join server");
        itemJoin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                User user = lvUser.getSelectionModel().getSelectedItem();
                Game server = lvGames.getSelectionModel().getSelectedItem();
                if(server != null)
                    ApplicationManager.connectServer(server, user.getIpAddress());
            }
        });
        ContextMenu contextUser = new ContextMenu(itemIP);
        lvUser.setItems(users);
        lvUser.setContextMenu(contextUser);
        lvUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                lvGames.setItems(ApplicationManager.getUserRunServers().get(newValue));
            }
        });
        lvUser.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY)
                    contextUser.show(lvUser, Side.BOTTOM, 0, 0);
            }
        });
        ContextMenu contextGame = new ContextMenu(itemIPAddress, itemJoin);
        lvGames.setContextMenu(contextGame);
        lvGames.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                Game game = lvGames.getSelectionModel().getSelectedItem();
                User user = lvUser.getSelectionModel().getSelectedItem();
                ApplicationManager.connectServer(game, user.getIpAddress());
            }
            if(event.getButton() == MouseButton.SECONDARY)
                contextGame.show(lvGames, Side.BOTTOM, 0, 0);
        });
        List<GameCell> cellFactory = new ArrayList<>();
        lvGames.setCellFactory(new Callback<ListView<Game>, ListCell<Game>>() {
            @Override
            public ListCell<Game> call(ListView<Game> param) {
                GameCell cell = new GameCell();
                cellFactory.add(cell);
                return cell;
            }
        });
    }

}
