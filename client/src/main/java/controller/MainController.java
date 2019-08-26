package controller;

import entities.game.Game;
import entities.game.GameList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import fxml.GameTile;

import java.util.HashMap;

public class MainController extends Controller{

    @FXML
    private StackPane spMain;
    @FXML
    private ScrollPane spServers, spGames;
    @FXML
    private Label lblStatus;
    @FXML
    private ImageView ivUsers, ivSettings, ivOrder;
    @FXML
    private Line seperator;
    @FXML
    private VBox rootGameServer;
    private ChangeListener<Boolean> statusListener;
    private HashMap<Game, ImageView> imageGameList;


    @FXML
    private void initialize(){
        imageGameList = new HashMap<>();
        addButtonHandler();
        updateGamePane();
        updateServerBrowserPane();
        if(getClient().getStatus().isConnected())
            lblStatus.setText("Connected to server: " + getClient().getStatus().getServerIP());
        else
            lblStatus.setText("Waiting for server connection.");
        statusListener = (observable, oldValue, newValue) -> {
            if(newValue)
                Platform.runLater(() -> lblStatus.setText("Connected to server: " + getClient().getStatus().getServerIP()));
            else
                Platform.runLater(() -> lblStatus.setText("Waiting for server connection."));
        };
        getClient().getStatus().getConnectedProperty().addListener(statusListener);
        Tooltip.install(ivUsers, new Tooltip("Open userlist"));
        Tooltip.install(ivSettings, new Tooltip("Open settings"));
        Tooltip.install(ivOrder, new Tooltip("Open food-ordering"));
        ivUsers.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivSettings.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivOrder.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivUsers.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivSettings.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivOrder.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
    }

    @FXML
    public void searchGame(KeyEvent event){
        //TODO: more than one key --> form a word
        ImageView image = null;
        int row = 0;
        for(int i = 0; i < getClient().getGames().size(); i++) {
            Game game = getClient().getGames().get(i);
            if (game.getName().toLowerCase().startsWith(event.getCharacter())) {
                image = imageGameList.get(game);
                row = i/5;
                break;
            }
        }
        if(image == null) {
            event.consume();
            return;
        }
        double height = spGames.getContent().getBoundsInLocal().getHeight();
        double y = (height/(getClient().getGames().size()/5))*row;
        spGames.setVvalue(y/height);
        System.out.println(height + " : " + y);
        image.requestFocus();
        event.consume();
    }

    private void addButtonHandler(){
        ivUsers.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showUsers();
        });
        ivSettings.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showSettings();
        });
        ivOrder.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showOrder();
        });
    }

    public void updateServerBrowserPane(){
        if(getClient().getUserRunServer().size() == 0){
            rootGameServer.getChildren().remove(seperator);
            rootGameServer.getChildren().remove(spServers);
            return;
        } else if(seperator.getParent() == null && spServers.getParent() == null){
            rootGameServer.getChildren().add(0, spServers);
            rootGameServer.getChildren().add(1, seperator);
        }
        seperator.setStartX(10);
        seperator.endXProperty().bind(spMain.widthProperty().subtract(20));
        GridPane serverGridPane = new GridPane();
        serverGridPane.setHgap(5);
        serverGridPane.minHeightProperty().bind(spMain.heightProperty().divide(8));
        getClient().getUserRunServer().forEach((user, games) -> {
            for(Game game : games){
                ImageView imageView = new ImageView(ControllerHelper.getIcon(game));
                imageView.setPreserveRatio(true);
                imageView.fitHeightProperty().bind(spMain.heightProperty().divide(8));
                imageView.setOnMouseClicked((event) -> {
                    if(event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY){
                        getClient().connectServer(game, user.getIpAddress(), true);
                    }
                });
                Tooltip.install(imageView, new Tooltip(user.getUsername()));
                serverGridPane.addRow(0, imageView);
            }
        });
        spServers.minHeightProperty().bind(spMain.heightProperty().divide(8).multiply(1.05));
        spServers.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                spServers.setHvalue(spServers.getHvalue() - event.getDeltaY() / (1.5*Math.abs(event.getDeltaY())*getClient().getGames().size()));
            }
        });
        spServers.setContent(serverGridPane);
    }

    public void updateGamePane(){
        GameList games = getClient().getGames();
        GridPane gameGridPane = new GridPane();
        gameGridPane.setHgap(15);
        gameGridPane.setVgap(15);
        for(int i = 0; i < games.size(); i++){
            Game game = games.get(i);
            Node gameTile = gameTile(game);
            gameGridPane.addRow(i/5, gameTile);
        }
        gameGridPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                spGames.setVvalue(spGames.getVvalue() - event.getDeltaY() / (Math.abs(event.getDeltaY())*games.size()/2.5));
            }
        });
        spGames.setContent(gameGridPane);
    }

    private Node gameTile(Game game){
        ImageView gameTileImage = new ImageView(ControllerHelper.getCover(game));
        gameTileImage.setPreserveRatio(false);
        gameTileImage.fitWidthProperty().bind(spGames.widthProperty().divide(5).subtract(60/5 + 1));
        gameTileImage.fitHeightProperty().bind(gameTileImage.fitWidthProperty().divide(0.725));
        imageGameList.put(game, gameTileImage);
        VBox gameTileOverlay = new GameTile(game, gameTileImage).getGameTile();
        gameTileOverlay.prefHeightProperty().bind(gameTileImage.fitHeightProperty());
        gameTileOverlay.prefWidthProperty().bind(gameTileImage.fitWidthProperty());
        gameTileOverlay.setVisible(false);
        StackPane gameTile = new StackPane(gameTileImage, gameTileOverlay);
        gameTile.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            gameTile.getChildren().get(1).setVisible(true);
            event.consume();
        });
        gameTile.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            gameTile.getChildren().get(1).setVisible(false);
            event.consume();
        });
        return gameTile;
    }

    private void mouseEntered(MouseEvent event){
        ImageView imageView = (ImageView)event.getTarget();
        if(imageView.equals(ivOrder))
            imageView.setImage(new Image(ClassLoader.getSystemResource("food_mo.png").toString(), true));
        else if(imageView.equals(ivSettings))
            imageView.setImage(new Image(ClassLoader.getSystemResource("config_mo.png").toString(), true));
        else if(imageView.equals(ivUsers))
            imageView.setImage(new Image(ClassLoader.getSystemResource("user_mo.png").toString(), true));
    }

    private void mouseExited(MouseEvent event){
        ImageView imageView = (ImageView)event.getTarget();
        if(imageView.equals(ivOrder))
            imageView.setImage(new Image(ClassLoader.getSystemResource("food.png").toString(), true));
        else if(imageView.equals(ivSettings))
            imageView.setImage(new Image(ClassLoader.getSystemResource("config.png").toString(), true));
        else if(imageView.equals(ivUsers))
            imageView.setImage(new Image(ClassLoader.getSystemResource("user.png").toString(), true));
    }

    @Override
    public void shutdown() {
        getClient().getStatus().getConnectedProperty().removeListener(statusListener);
    }
}
