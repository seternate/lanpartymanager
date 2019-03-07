package controller;

import entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderController {
    ObservableList<User> userorders;

    @FXML
    private TableView<User> tvOrdering;
    @FXML
    private TableColumn<User, String> tcUser, tcOrder;
    @FXML
    private TextField txtOrder;

    @FXML
    private void initialize(){
        List<User> orders = new ArrayList(Arrays.asList(ApplicationManager.getUserslist().toArray()));
        for(User order : orders){
            if(order.getOrder() == null || order.getOrder().isEmpty())
                orders.remove(order);
        }
        userorders = FXCollections.observableArrayList(orders);
        ApplicationManager.getUserslist().addListener(new ListChangeListener<User>() {
            @Override
            public void onChanged(Change<? extends User> c) {
                for(User user : c.getRemoved())
                    userorders.remove(user);
                for(User user : c.getAddedSubList()){
                    if(user.getOrder() != null && !user.getOrder().isEmpty())
                        userorders.add(user);
                }
            }
        });
        tcUser.setCellValueFactory(param -> {
            if(param.getValue().getOrder() != null || param.getValue().getOrder().isEmpty())
                new SimpleStringProperty(param.getValue().getUsername());
            return null;
        });
        tcOrder.setCellValueFactory(param -> {
            if(param.getValue().getOrder() != null || param.getValue().getOrder().isEmpty())
                new SimpleStringProperty(param.getValue().getOrder());
            return null;
        });
        tvOrdering.setItems(userorders);
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
