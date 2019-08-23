package controller;

import entities.user.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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

        tcOrder.setCellFactory(tablecolm -> {
            TableCell<User,String> tableCell = new TableCell<User,String>() {
                @Override protected void updateItem(String item, boolean empty) {
                    if (item == getItem()) return;

                    super.updateItem(item, empty);

                    if (item == null) {
                        super.setText(null);
                        super.setGraphic(null);
                    } else {
                        super.setText(null);
                        Label l = new Label(item);
                        l.setStyle("-fx-text-fill: white; -fx-font: 12 System;");
                        l.setWrapText(true);
                        VBox box = new VBox(l);
                        l.heightProperty().addListener((observable,oldValue,newValue)-> {
                            box.setPrefHeight(newValue.doubleValue()+7);
                            Platform.runLater(()->this.getTableRow().requestLayout());
                        });
                        super.setGraphic(box);
                    }
                }
            };
            return tableCell;
        });

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
