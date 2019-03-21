package controller;

import entities.user.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;

public class UsersController {
    private class UserCell extends ListCell<User> {

        UserCell(){
            setOnDragOver(event -> {
                if (event.getDragboard().hasFiles() && getItem() != null) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });
            setOnDragDropped(event -> {
                ApplicationManager.sendFiles(getItem(), event.getDragboard().getFiles());
                event.consume();
            });
        }

        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if(item != null){
                setText(item.getUsername());
            }
        }
    }
    @FXML
    private ListView<User> lvUsers;

    @FXML
    private void initialize(){
        lvUsers.setItems(ApplicationManager.getUserslist());
        lvUsers.setCellFactory(c -> new UserCell());

        MenuItem itemUsername = new MenuItem("Copy IP-Address");
        itemUsername.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(lvUsers.getSelectionModel().getSelectedItem().getIpAddress());
                clipboard.setContent(content);
            }
        });

        ContextMenu context = new ContextMenu(itemUsername);
        lvUsers.setContextMenu(context);

        lvUsers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY)
                    context.show(lvUsers, Side.BOTTOM, 0, 0);
            }
        });

    }

}
