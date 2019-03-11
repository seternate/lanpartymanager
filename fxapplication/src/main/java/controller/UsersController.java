package controller;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;

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
    }

}
