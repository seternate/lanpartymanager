package controller;

import entities.game.Game;
import entities.game.GameList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.io.IOException;

public class MainController extends Controller{

    @FXML
    private StackPane spMain;
    @FXML
    private ScrollPane spServers, spGames;
    @FXML
    private Label lblStatus;
    @FXML
    private ImageView ivUsers, ivSettings, ivOrder, ivServerbrowser;
    @FXML
    private Line seperator;
    private ChangeListener<Boolean> statusListener;


    @FXML
    private void initialize(){
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
        Tooltip.install(ivServerbrowser, new Tooltip("Open serverbrowser"));
        ivUsers.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivSettings.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivOrder.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivServerbrowser.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivUsers.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivSettings.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivOrder.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivServerbrowser.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
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
        ivServerbrowser.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showServerBrowser();
        });

    }

    private void updateServerBrowserPane(){
        //TODO: GameList mit allen offenen Servern, dazu noch Tooltip mit Spieler der ihn geöffnet hat
        //TODO: nicht anzeigen wenn keine Server vorhanden sind
        seperator.setStartX(10);
        seperator.endXProperty().bind(spMain.widthProperty().subtract(20));
        GameList games = getClient().getGames();
        GridPane serverGridPane = new GridPane();
        serverGridPane.setVgap(30);
        serverGridPane.minHeightProperty().bind(spMain.widthProperty().divide(5));
        for(int i = 0; i < games.size(); i++){
            ImageView imageView = new ImageView(ControllerHelper.getIcon(games.get(i)));
            imageView.setPreserveRatio(true);
            imageView.fitHeightProperty().bind(spMain.widthProperty().divide(5));
            serverGridPane.addRow(0, imageView);
        }
        spServers.minHeightProperty().bind(spMain.widthProperty().divide(5).multiply(1.05));
        spServers.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                spServers.setHvalue(spServers.getHvalue() - event.getDeltaY() / (1.5*Math.abs(event.getDeltaY())*games.size()));
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
            Node gameTile = gameTile(games.get(i));
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
        VBox gameTileOverlay = gameTileOverlay(gameTileImage, game);
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

    private VBox gameTileOverlay(ImageView gameTileImage, Game game){
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("gameoverlay.fxml"));
        loader.setController(new GameOverlayController(gameTileImage, game));
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void mouseEntered(MouseEvent event){
        ImageView imageView = (ImageView)event.getTarget();
        if(imageView.equals(ivOrder))
            imageView.setImage(new Image(ClassLoader.getSystemResource("food_mo.png").toString(), true));
        else if(imageView.equals(ivServerbrowser))
            imageView.setImage(new Image(ClassLoader.getSystemResource("serverbrowser_mo.png").toString(), true));
        else if(imageView.equals(ivSettings))
            imageView.setImage(new Image(ClassLoader.getSystemResource("config_mo.png").toString(), true));
        else if(imageView.equals(ivUsers))
            imageView.setImage(new Image(ClassLoader.getSystemResource("user_mo.png").toString(), true));
    }

    private void mouseExited(MouseEvent event){
        ImageView imageView = (ImageView)event.getTarget();
        if(imageView.equals(ivOrder))
            imageView.setImage(new Image(ClassLoader.getSystemResource("food.png").toString(), true));
        else if(imageView.equals(ivServerbrowser))
            imageView.setImage(new Image(ClassLoader.getSystemResource("serverbrowser.png").toString(), true));
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
