package controller;

import entities.game.Game;
import client.monitor.GameStatus;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Locale;

public class GameOverlayController extends Controller{

    private ImageView gameTileImage;
    private Game game;
    private GameStatus gameStatus;
    @FXML
    private ImageView ivRunGame, ivDownloadGame, ivOpenExplorer, ivStartServer;
    @FXML
    private Label lblGamename, lblVersion, lblDownloadbar, lblDownloadSpeed;
    @FXML
    private ProgressBar pbDownload;
    @FXML
    private StackPane spDownloadGame;
    @FXML
    private GridPane gpGameTile;
    @FXML
    private VBox root;


    public GameOverlayController(ImageView gameTileImage, Game game){
        this.game = game;
        this.gameTileImage = gameTileImage;
    }

    @FXML
    private void initialize() {
        gameStatus = getClient().getGameStatus(game);
        lblGamename.setText(game.getName());
        lblVersion.setText(game.getVersionServer());
        Tooltip.install(lblGamename, new Tooltip(lblGamename.getText()));
        if (!lblVersion.getText().isEmpty())
            Tooltip.install(lblVersion, new Tooltip("Version on the server"));
        Tooltip.install(ivRunGame, new Tooltip("Start game"));
        Tooltip.install(ivDownloadGame, new Tooltip("Download game"));
        Tooltip.install(ivOpenExplorer, new Tooltip("Open game folder"));
        Tooltip.install(ivStartServer, new Tooltip("Start a new server"));
        ivRunGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!gameStatus.isRunning())
                    getClient().startGame(game, true);
                else
                    getClient().stopGame(game);
            }
            event.consume();
        });
        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!gameStatus.isDownloading() && !gameStatus.isExtracting())
                    getClient().download(game);
                else {
                    getClient().stopDownloadUnzip(game);
                    lblDownloadbar.setText("Stopping ...");
                }
            }
            event.consume();
        });
        ivOpenExplorer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                getClient().openExplorer(game);
            event.consume();
        });
        ivStartServer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (game.isOpenServer() && event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.openServerStartup(game);
            event.consume();
        });
        spDownloadGame.setVisible(false);
        lblDownloadSpeed.setVisible(false);
        if(!game.isOpenServer())
            ivStartServer.setImage(new Image(ClassLoader.getSystemResource("serverplay_md.png").toString(), true));
        lblGamename.setFont(Font.font("System", FontWeight.BOLD, gameTileImage.fitHeightProperty().doubleValue()/17.0));
        lblVersion.setFont(Font.font("System", FontWeight.BOLD, gameTileImage.fitHeightProperty().doubleValue()/17.0));
        lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, gpGameTile.heightProperty().doubleValue()*0.45/5));
        lblDownloadSpeed.setFont(Font.font("System", FontWeight.NORMAL, gpGameTile.heightProperty().doubleValue()*0.25/5));
        ivRunGame.fitHeightProperty().bind(gpGameTile.heightProperty().divide(4));
        ivDownloadGame.fitHeightProperty().bind(gpGameTile.heightProperty().divide(4));
        ivOpenExplorer.fitHeightProperty().bind(gpGameTile.heightProperty().divide(4));
        ivStartServer.fitHeightProperty().bind(gpGameTile.heightProperty().divide(4));
        ivRunGame.fitWidthProperty().bind(gpGameTile.heightProperty().divide(4));
        ivDownloadGame.fitWidthProperty().bind(gpGameTile.heightProperty().divide(4));
        ivOpenExplorer.fitWidthProperty().bind(gpGameTile.heightProperty().divide(4));
        ivStartServer.fitWidthProperty().bind(gpGameTile.heightProperty().divide(4));
        ivRunGame.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivStartServer.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivOpenExplorer.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivRunGame.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivOpenExplorer.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivStartServer.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        if(gameStatus.isDownloading() || gameStatus.isExtracting()){
            ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
        }
        if(gameStatus.isLocal()){
            lblGamename.setTextFill(Paint.valueOf("black"));
            lblVersion.setTextFill(Paint.valueOf("black"));
        } else {
            lblGamename.setTextFill(Color.web("#555555"));
            lblVersion.setTextFill(Color.web("#555555"));
        }
        gameTileImage.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            double fontSize = newValue.doubleValue()/17.0;
            lblGamename.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            lblVersion.setFont(Font.font("System", FontWeight.BOLD, fontSize));
        });
        gpGameTile.heightProperty().addListener((observable, oldValue, newValue) -> {
            lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, newValue.doubleValue()*0.45/5));
            lblDownloadSpeed.setFont(Font.font("System", FontWeight.NORMAL, newValue.doubleValue()*0.25/5));
        });
        gameStatus.getDownloadingProperty().addListener((observable, oldValue, newValue) -> setDownloadbarVisibility(newValue));
        gameStatus.getDownloadProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() > 0) {
                spDownloadGame.setVisible(true);
                lblDownloadSpeed.setVisible(true);
                Platform.runLater(() -> {
                    pbDownload.setProgress(newValue.doubleValue());
                    lblDownloadbar.setText(String.format(Locale.ENGLISH,"%.0f %%", newValue.doubleValue() * 100));
                });
            }
        });
        gameStatus.getExtractingProperty().addListener((observable, oldValue, newValue) -> setDownloadbarVisibility(newValue));
        gameStatus.getExtractionProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() > 0) {
                spDownloadGame.setVisible(true);
                Platform.runLater(() -> {
                    pbDownload.setProgress(newValue.doubleValue());
                    lblDownloadbar.setText(String.format(Locale.ENGLISH, "Unzip: %.0f %%", newValue.doubleValue() * 100));
                });
            }
        });
        MenuItem itemDeleteGame = new MenuItem("Delete");
        itemDeleteGame.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if(gameStatus.isRunning())
                    getClient().stopGame(game);
                if(gameStatus.isDownloading() ||gameStatus.isExtracting())
                    getClient().stopDownloadUnzip(game);
                if(game.delete()) {
                    gameStatus.setLocal(false);
                    gameStatus.setPlayable(false);
                }

            }
        });
        ContextMenu context = new ContextMenu(itemDeleteGame);
        root.setOnContextMenuRequested((event) -> {
            if(gameStatus.isLocal()) {
                context.show(gameTileImage.getScene().getWindow(), event.getScreenX(), event.getScreenY());
            }
        });
        gameStatus.getLocalProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                lblGamename.setTextFill(Color.web("#000000", 0.75));
                lblVersion.setTextFill(Color.web("#000000", 0.75));
            } else {
                lblGamename.setTextFill(Paint.valueOf("black"));
                lblVersion.setTextFill(Paint.valueOf("black"));
            }
        });
        gameStatus.getDownloadSpeedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> lblDownloadSpeed.setText(newValue));
        });
        gameStatus.getRunningProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                Platform.runLater(() -> {
                    if(ivRunGame.hoverProperty().get())
                        ivRunGame.setImage(new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true));
                    else
                        ivRunGame.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
                    Tooltip.install(ivRunGame, new Tooltip("Exit game"));
                });
            } else {
                Platform.runLater(() -> {
                    if(ivRunGame.hoverProperty().get())
                        ivRunGame.setImage(new Image(ClassLoader.getSystemResource("play_mo.png").toString(), true));
                    else
                        ivRunGame.setImage(new Image(ClassLoader.getSystemResource("play.png").toString(), true));
                    Tooltip.install(ivRunGame, new Tooltip("Start game"));
                });
            }
        });

    }

    private void setDownloadbarVisibility(Boolean newValue) {
        if(!newValue){
            Tooltip.install(ivDownloadGame, new Tooltip("Download game"));
            spDownloadGame.setVisible(false);
            lblDownloadSpeed.setVisible(false);
            if(!ivDownloadGame.hoverProperty().get())
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("serverdownload_mo.png").toString(), true));
            else
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("serverdownload.png").toString(), true));
        } else {
            Tooltip.install(ivDownloadGame, new Tooltip("Stop download/extraction"));
            if(ivDownloadGame.hoverProperty().get())
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true));
            else
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
        }
    }

    private void mouseEntered(MouseEvent event){
        GameStatus gameStatus = getClient().getGameStatus(game);
        if((event.getTarget().equals(ivStartServer) && game.isOpenServer()) || event.getTarget().equals(ivRunGame) ||
                event.getTarget().equals(ivDownloadGame) || event.getTarget().equals(ivOpenExplorer)) {
            ImageView imageView = (ImageView)event.getTarget();
            if(imageView.equals(ivRunGame) && !gameStatus.isRunning())
                imageView.setImage(new Image(ClassLoader.getSystemResource("play_mo.png").toString(), true));
            else if(imageView.equals(ivRunGame) && gameStatus.isRunning())
                imageView.setImage(new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && !gameStatus.isDownloading() && !gameStatus.isExtracting())
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverdownload_mo.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && (gameStatus.isDownloading() || gameStatus.isExtracting()))
                imageView.setImage(new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true));
            else if(imageView.equals(ivOpenExplorer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("folder_mo.png").toString(), true));
            else if(imageView.equals(ivStartServer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverplay_mo.png").toString(), true));
        }
    }

    private void mouseExited(MouseEvent event){
        GameStatus gameStatus = getClient().getGameStatus(game);
        if((event.getTarget().equals(ivStartServer) && game.isOpenServer()) || event.getTarget().equals(ivRunGame) ||
                event.getTarget().equals(ivDownloadGame) || event.getTarget().equals(ivOpenExplorer)) {
            ImageView imageView = (ImageView)event.getTarget();
            if(imageView.equals(ivRunGame) && !gameStatus.isRunning())
                imageView.setImage(new Image(ClassLoader.getSystemResource("play.png").toString(), true));
            else if(imageView.equals(ivRunGame) && gameStatus.isRunning())
                imageView.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && !gameStatus.isDownloading() && !gameStatus.isExtracting())
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverdownload.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && (gameStatus.isDownloading() || gameStatus.isExtracting()))
                imageView.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
            else if(imageView.equals(ivOpenExplorer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("folder.png").toString(), true));
            else if(imageView.equals(ivStartServer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverplay.png").toString(), true));
        }
    }

    @Override
    public void shutdown() {

    }

}
