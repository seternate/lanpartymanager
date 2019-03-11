package controller;

import entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class OrderController {

    @FXML
    private TableView<User> tvOrdering;
    @FXML
    private TableColumn<User, String> tcUser, tcOrder;
    @FXML
    private TextField txtOrder;

    @FXML
    private void initialize(){
        tcUser.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUsername()));
        tcOrder.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOrder()));
        tvOrdering.setItems(ApplicationManager.getOrderList());
        txtOrder.setText(ApplicationManager.getUser().getOrder());
    }

    @FXML
    private void placeOrder(MouseEvent event){
        if(event.getButton() == MouseButton.PRIMARY)
            ApplicationManager.setOrder(txtOrder.getText());
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            ApplicationManager.setOrder(txtOrder.getText());
    }
}
