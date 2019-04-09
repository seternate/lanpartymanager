package controller;

import entities.game.Game;
import entities.user.User;
import javafx.application.Platform;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
            this.setGraphicTextGap(10);
            setGraphic(null);
            setText(null);
            if(item != null){
                setText(item.getUsername());
                if(ApplicationManager.getUserRunGames().get(item) != null){
                    Image image = UsersController.getIcon(ApplicationManager.getUserRunGames().get(item));
                    ImageView icon = new ImageView(image);
                    icon.setFitHeight(56);
                    icon.setFitWidth(56);
                    setGraphic(icon);
                }
            }
        }

    }


    private static Logger log = Logger.getLogger(UsersController.class);

    @FXML
    private ListView<User> lvUsers;


    static Image getIcon(Game game){
        File iconpath = new File(ApplicationManager.getGamepath() + "/images");
        if (iconpath.listFiles() != null) {
            for (File icon : iconpath.listFiles()) {
                int index = icon.getName().lastIndexOf(".");
                if (icon.getName().substring(0, index).equals(game.getName() + "_icon")) {
                    log.info("Local icon of '" + game + "' found.");
                    return new Image("file:" + icon.getAbsolutePath(), true);
                }
            }
        }
        return new Image(ClassLoader.getSystemResource("dummyicon.png").toString(), true);
    }


    /**
     * Initializing UserController.
     */
    @FXML
    private void initialize(){
        log.info("Initializing.");
        //Bind ObservableList from client to show users
        lvUsers.setItems(ApplicationManager.getUserslist());
        List<UserCell> cellFactory = new ArrayList<>();
        lvUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                UserCell cell = new UserCell();
                cellFactory.add(cell);
                return cell;
            }
        });
        MenuItem itemIPAddress = new MenuItem("Copy IP to clipboard");
        itemIPAddress.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(lvUsers.getSelectionModel().getSelectedItem().getIpAddress());
                clipboard.setContent(content);
            }
        });
        MenuItem itemJoin = new MenuItem("Join open server");
        itemJoin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                User user = lvUsers.getSelectionModel().getSelectedItem();
                ObservableList<Game> servers = ApplicationManager.getUserRunServers().get(user);
                Game game = servers.get(servers.size() - 1);
                if(game != null)
                    ApplicationManager.connectServer(game, user.getIpAddress());
            }
        });
        ContextMenu context = new ContextMenu(itemIPAddress, itemJoin);
        lvUsers.setContextMenu(context);
        lvUsers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY)
                    context.show(lvUsers, Side.BOTTOM, 0, 0);
            }
        });
        ApplicationManager.getUserRunGames().addListener(new MapChangeListener<User, Game>() {
            @Override
            public void onChanged(Change<? extends User, ? extends Game> change) {
                Image image = null;
                if(change.getValueAdded() != null){
                    image = UsersController.getIcon(change.getValueAdded());
                }
                ImageView icon = new ImageView(image);
                icon.setFitHeight(48);
                icon.setFitWidth(48);
                for(UserCell cell : cellFactory){
                    if(image != null && cell.getItem() != null && cell.getItem().equals(change.getKey()))
                        Platform.runLater(() -> {
                            cell.setGraphic(icon);
                        });
                    else if(image == null && cell.getItem() != null && cell.getItem().equals((change.getKey())))
                        Platform.runLater(() -> cell.setGraphic(null));
                }
            }
        });

    }

}
