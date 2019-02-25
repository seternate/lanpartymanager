package controller;

import entities.User;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class UsersController {
    @FXML
    private ListView<User> lvUsers;

    @FXML
    private void initialize(){
        lvUsers.setItems(ApplicationManager.getUserslist());
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
    }

}
