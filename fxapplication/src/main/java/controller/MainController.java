package controller;

import entities.game.Game;
import entities.game.GameList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class MainController {
    public volatile Game focusedGame;

    @FXML
    private ScrollPane spMain;
    @FXML
    private Label lblStatus, lblFileStatus;
    @FXML
    private ImageView ivUsers, ivSettings, ivOrder;

    @FXML
    private void initialize(){
        Tooltip.install(ivUsers, new Tooltip("Connected users"));
        Tooltip.install(ivSettings, new Tooltip("Open settings"));
        ApplicationManager.setServerStatusLabel(lblStatus);
        ApplicationManager.setFileStatusLabel(lblFileStatus);
        spMain.setFitToWidth(true);
        updateGamePane();
        addButtonHandler();

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

    public void updateGamePane(){
        GameList games = ApplicationManager.getGames();
        GridPane tilePane = new GridPane();
        tilePane.setHgap(20);
        tilePane.setVgap(30);
        for(int i = 0; i < games.size(); i++){
            Node gameTile = gameTile(games.get(i));
            tilePane.addRow(i/3, gameTile);
            tilePane.setHgrow(gameTile, Priority.ALWAYS);
            tilePane.setHalignment(gameTile, HPos.CENTER);
        }
        spMain.setContent(tilePane);
    }

    private Node gameTile(Game game){
        ImageView gameTileImage = new ImageView(getGameCover(game));
        gameTileImage.setPreserveRatio(false);
        gameTileImage.fitWidthProperty().bind(spMain.widthProperty().divide(3.5));
        gameTileImage.fitHeightProperty().bind(spMain.widthProperty().multiply(0.4));

        VBox gameTileOverlay = gameTileOverlay(gameTileImage, game);
        gameTileOverlay.prefHeightProperty().bind(gameTileImage.fitHeightProperty());
        gameTileOverlay.prefWidthProperty().bind(gameTileImage.fitWidthProperty());
        gameTileOverlay.setVisible(false);

        StackPane gameTile = new StackPane(gameTileImage, gameTileOverlay);
        gameTile.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            focusedGame = game;
            gameTile.getChildren().get(1).setVisible(true);
            event.consume();
        });
        gameTile.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            gameTile.getChildren().get(1).setVisible(false);
            event.consume();
        });
        return gameTile;
    }

    private Image getGameCover(Game game) {
        File coverpath = new File(ApplicationManager.getGamepath() + "/cover");
        if(!game.getCoverUrl().isEmpty())
            return new Image(game.getCoverUrl(), true);
        for(File cover : coverpath.listFiles()){
            int index = cover.getName().lastIndexOf(".");
            if(cover.getName().substring(0, index).equals(game.getName())) {
                return new Image("file:" + cover.getAbsolutePath(), true);
            }
        }
        return new Image(ClassLoader.getSystemResource("dummycover.jpg").toString(),true);
    }

    private VBox gameTileOverlay(ImageView gameTileImage, Game game){
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("gameoverlay.fxml"));
        loader.setController(new GameOverlayController(gameTileImage, game));
        try {
            VBox rootNode = loader.load();
            return rootNode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
