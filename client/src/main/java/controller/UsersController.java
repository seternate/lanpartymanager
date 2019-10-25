package controller;

import entities.game.Game;
import entities.user.User;
import javafx.application.Platform;
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
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class UsersController extends Controller{

    private class UserCell extends ListCell<User>{

        UserCell(){
            setOnDragOver(event -> {
                if (event.getDragboard().hasFiles() && getItem() != null) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });
            setOnDragDropped(event -> {
                getClient().sendFiles(getItem(), event.getDragboard().getFiles());
                event.consume();
            });
        }

        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            this.setGraphicTextGap(10);
            setGraphic(null);
            setText(null);
            if(item != null){
                setText(item.getUsername() + " (" + item.getIpAddress()+ ")");
                if(getClient().getUserRunGames().get(item) != null){
                    Image image = ControllerHelper.getIcon(getClient().getUserRunGames().get(item).get(0));
                    ImageView icon = new ImageView(image);
                    Tooltip tooltip = new Tooltip(getClient().getUserRunGames().get(item).toString());
                    this.setTooltip(tooltip);
                    icon.setFitHeight(56);
                    icon.setFitWidth(56);
                    setGraphic(icon);
                }
            }
        }
    }


    @FXML
    private ListView<User> lvUsers;
    private ObservableList<User> users;
    private List<UserCell> cellFactory;


    @FXML
    private void initialize(){
        users = FXCollections.observableArrayList();
        users.addAll(getClient().getUserList().toList());
        lvUsers.setItems(users);
        cellFactory = new ArrayList<>();
        lvUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                UserCell cell = new UserCell();
                cellFactory.add(cell);
                return cell;
            }
        });
        MenuItem itemIPAddress = new MenuItem("Copy IP");
        itemIPAddress.setOnAction(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(lvUsers.getSelectionModel().getSelectedItem().getIpAddress());
                clipboard.setContent(content);
        });
        MenuItem itemJoin = new MenuItem("Join server");
        itemJoin.setOnAction(event -> {
                User user = lvUsers.getSelectionModel().getSelectedItem();
                List<Game> servers = getClient().getUserRunServer().get(user);
                Game game = servers.get(servers.size() - 1);
                if(game != null){
                    getClient().connectServer(game, user.getIpAddress(), true);
            }
        });
        ContextMenu context = new ContextMenu(itemIPAddress, itemJoin);
        lvUsers.setContextMenu(context);
        lvUsers.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.SECONDARY)
                    context.show(lvUsers, Side.BOTTOM, 0, 0);
        });
        getClient().getUserRunGames().forEach((user, games) -> {
            Image image = ControllerHelper.getIcon(games.get(games.size() - 1));
            ImageView icon = new ImageView(image);
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            for(UserCell cell : cellFactory){
                if(cell.getItem() != null && cell.getItem().equals(user))
                    Platform.runLater(() -> {
                        cell.setGraphic(icon);
                        Tooltip tooltip = new Tooltip("Playing: " + games.get(games.size() - 1));
                        cell.setTooltip(tooltip);
                    });
            }
        });
    }

    public void update(){
        Platform.runLater(() -> users.setAll(getClient().getUserList().toList()));
        for(UserCell cell : cellFactory){
            if(cell.getItem() != null)
                Platform.runLater(() -> {
                    cell.setGraphic(null);
                    cell.setTooltip(null);
                });
        }
        getClient().getUserRunGames().forEach((user, games) -> {
            Image image = ControllerHelper.getIcon(games.get(games.size() - 1));
            ImageView icon = new ImageView(image);
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            for(UserCell cell : cellFactory){
                if(cell.getItem() != null && cell.getItem().equals(user))
                    Platform.runLater(() -> {
                        cell.setGraphic(icon);
                        Tooltip tooltip = new Tooltip("Playing: " + games.get(games.size() - 1));
                        cell.setTooltip(tooltip);
                    });
            }
        });
    }

    @Override
    public void shutdown() {

    }

}
