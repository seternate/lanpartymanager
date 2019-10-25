package controller;

import entities.game.Game;
import entities.game.serverparameters.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

public class ServerDetailController extends Controller {
    private Game game;
    @FXML
    private ScrollPane spParameters;
    @FXML
    private GridPane gpParameters;


    public ServerDetailController(Game game){
        this.game = game;
    }

    @FXML
    private void initialize(){
        gpParameters = new GridPane();
        gpParameters.setHgap(20);
        gpParameters.setVgap(10);
        gpParameters.setPadding(new Insets(5));
        spParameters.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                gpParameters.setPrefWidth(newValue.doubleValue() - 5);
            }
        });
        for(int i = 0; i < game.getServerParameters().size(); i++){
            ServerParameter parameter = game.getServerParameters().get(i);
            Label label = new Label(parameter.getName());
            Node argument;
            ServerParameterType parameterType = parameter.getType();
            switch(parameterType){
                case DROPDOWN: argument = initDropdown((ServerParameterDropdown)parameter); break;
                case LITERAL:  argument = initLiteral((ServerParameterLiteral)parameter); break;
                case NUMBER:   argument = initNumber((ServerParameterNumber)parameter); break;
                case BOOLEAN:  argument = initBoolean((ServerParameterBoolean)parameter); break;
                default: argument = new Label("Can not initialize argument.");
            }
            gpParameters.addRow(i, label, argument);
        }
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setFillWidth(true);
        col2.setHgrow(Priority.ALWAYS);
        gpParameters.getColumnConstraints().addAll(col1, col2);
        spParameters.setContent(gpParameters);
    }

    private Node initDropdown(ServerParameterDropdown parameter){
        ObservableList<String> dropdownParameters = FXCollections.observableArrayList(parameter.getDropdownText());
        ComboBox<String> dropdown = new ComboBox<>();
        dropdown.setItems(dropdownParameters);
        dropdown.getSelectionModel().selectFirst();
        dropdown.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return dropdown;
    }

    private Node initLiteral(ServerParameterLiteral parameter){
        return new TextField(parameter.getStandard());
    }

    private Node initNumber(ServerParameterNumber parameter){
        return new TextField(parameter.getStandard());
    }

    private Node initBoolean(ServerParameterBoolean paramter){
        HBox root = new HBox();
        root.setSpacing(10);
        RadioButton yes = new RadioButton("Yes");
        RadioButton no = new RadioButton("No");
        ToggleGroup tgrp = new ToggleGroup();
        yes.setToggleGroup(tgrp);
        no.setToggleGroup(tgrp);
        yes.setSelected(true);
        root.getChildren().addAll(yes, no);
        return root;
    }

    @FXML
    public void startServer(){
        StringBuilder parameters = new StringBuilder();
        for(int i = 0; i < gpParameters.getChildren().size(); i++){
            if(i%2 != 0) {
                Node children = gpParameters.getChildren().get(i);
                if (children.getClass() == TextField.class)
                    parameters.append(game.getServerParameters().get(GridPane.getRowIndex(children)).getArgument(((TextField)children).getText())).append(" ");
                else if(children.getClass() == ComboBox.class)
                    parameters.append(game.getServerParameters().get(GridPane.getRowIndex(children)).getArgument(((ComboBox<String>)children).getSelectionModel().getSelectedItem())).append(" ");
                else if(children.getClass() == HBox.class){
                    if(((RadioButton)((HBox)children).getChildren().get(0)).isSelected())
                        parameters.append(game.getServerParameters().get(GridPane.getRowIndex(children)).getArgument(((RadioButton)((HBox)children).getChildren().get(0)).getText())).append(" ");
                    else if(((RadioButton)((HBox)children).getChildren().get(1)).isSelected())
                        parameters.append(game.getServerParameters().get(GridPane.getRowIndex(children)).getArgument(((RadioButton)((HBox)children).getChildren().get(1)).getText())).append(" ");
                }
            }
        }
        getClient().startServer(game, parameters.toString(), true);
        spParameters.getScene().getWindow().hide();
    }

    @FXML
    private void enter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            startServer();
    }

    @Override
    public void shutdown() {

    }
}
