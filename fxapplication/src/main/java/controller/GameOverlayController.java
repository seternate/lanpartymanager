package controller;

import entities.game.Game;
import clientInterface.GameStatusProperty;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.log4j.Logger;

import java.util.Locale;

class GameOverlayController {
    private static Logger log = Logger.getLogger(GameOverlayController.class);


    private ImageView gameTileImage;
    private Game game;
    private GameStatusProperty gameStatus;
    @FXML
    private ImageView ivRunGame, ivDownloadGame, ivOpenExplorer, ivConnectServer, ivStartServer;
    @FXML
    private Label lblGamename, lblVersion, lblDownloadbar, lblDownloadSpeed;
    @FXML
    private ProgressBar pbDownload;
    @FXML
    private StackPane spDownloadGame;
    @FXML
    private GridPane gpGameTile;


    public GameOverlayController(ImageView gameTileImage, Game game){
        this.game = game;
        this.gameTileImage = gameTileImage;
    }

    @FXML
    private void initialize(){
        log.info("Initializing gametileoverlay for '" + game + "'.");
        //Getting the gamestatus variable to listen to for listener implementation for downloading and unzipping.
        gameStatus = ApplicationManager.getGamestatusProperty();
        //Set the gamename and gameversion label
        lblGamename.setText(game.getName());
        lblVersion.setText(game.getVersionServer());
        //Install all tooltips
        Tooltip.install(lblGamename, new Tooltip(lblGamename.getText()));
        if(!lblVersion.getText().isEmpty())
            Tooltip.install(lblVersion, new Tooltip("Gameversion on the server"));
        Tooltip.install(ivRunGame, new Tooltip("Start game"));
        Tooltip.install(ivDownloadGame, new Tooltip("Download game"));
        Tooltip.install(ivOpenExplorer, new Tooltip("Open game folder"));
        Tooltip.install(ivConnectServer, new Tooltip("Connect to an open server"));
        Tooltip.install(ivStartServer, new Tooltip("Start a new server"));
        //EventHandler for the buttons
        ivRunGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                if(!gameStatus.running.getValue())
                    ApplicationManager.startGame(game);
                else
                    ApplicationManager.stopGame(game);
            event.consume();
        });
        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                if (!gameStatus.downloading.getValue() && !gameStatus.unzipping.getValue())
                    ApplicationManager.downloadGame(game);
                else
                    ApplicationManager.stopDownloadUnzip(game);
            }
            event.consume();
        });
        ivOpenExplorer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.openExplorer(game);
            event.consume();
        });
        ivConnectServer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(game.isConnectDirect() && event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.openServerList(game);
            event.consume();
        });
        ivStartServer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(game.isOpenServer() && event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.openServerStartup(game);
            event.consume();
        });
        //Download listener
        gameStatus.downloading.addListener((observable, oldValue, newValue) -> setDownloadbarVisibility(newValue));
        gameStatus.downloadProgress.addListener((observable, oldValue, newValue) -> {
            if(ApplicationManager.getFocusedGame().equals(game) && newValue.doubleValue() > 0) {
                spDownloadGame.setVisible(true);
                lblDownloadSpeed.setVisible(true);
                Platform.runLater(() -> {
                    pbDownload.setProgress(newValue.doubleValue());
                    lblDownloadbar.setText(String.format(Locale.ENGLISH,"%.01f %%", newValue.doubleValue() * 100));
                });
            }
        });
        //Unzip listener
        gameStatus.unzipping.addListener((observable, oldValue, newValue) -> setDownloadbarVisibility(newValue));
        gameStatus.unzipProgress.addListener((observable, oldValue, newValue) -> {
            if(ApplicationManager.getFocusedGame().equals(game) && newValue.doubleValue() > 0) {
                spDownloadGame.setVisible(true);
                Platform.runLater(() -> {
                    pbDownload.setProgress(newValue.doubleValue());
                    lblDownloadbar.setText(String.format(Locale.ENGLISH, "Unzip: %.01f %%", newValue.doubleValue() * 100));
                });
            }
        });
        //Local availability listener
        gameStatus.local.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) {
                    Platform.runLater(() -> lblGamename.setTextFill(Paint.valueOf("grey")));
                    Platform.runLater(() -> lblVersion.setTextFill(Paint.valueOf("grey")));
                } else {
                    Platform.runLater(() -> lblGamename.setTextFill(Paint.valueOf("black")));
                    Platform.runLater(() -> lblVersion.setTextFill(Paint.valueOf("black")));
                }
            }

        });
        //DownloadSpeedListener
        gameStatus.downloadSpeed.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Platform.runLater(() -> lblDownloadSpeed.setText(newValue));
            }
        });
        //Game running listener
        gameStatus.running.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
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
            }
        });
        //Label fontsizing
        gameTileImage.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            //Font resizing
            double fontSize = newValue.doubleValue()/17.0;
            lblGamename.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            lblVersion.setFont(Font.font("System", FontWeight.BOLD, fontSize));
        });
        //Hiding downloadbar and downloadspeedlabel
        spDownloadGame.setVisible(false);
        lblDownloadSpeed.setVisible(false);
        //Setting the progressbarlabel & downloadspeedlabel fontsize
        gpGameTile.heightProperty().addListener((observable, oldValue, newValue) -> {
            lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, newValue.doubleValue()*0.5/5));
            lblDownloadSpeed.setFont(Font.font("System", FontWeight.NORMAL, newValue.doubleValue()*0.25/5));
        });
        //Set the button sizing
        ivRunGame.fitHeightProperty().bind(gpGameTile.heightProperty().divide(5));
        ivDownloadGame.fitHeightProperty().bind(gpGameTile.heightProperty().divide(5));
        ivOpenExplorer.fitHeightProperty().bind(gpGameTile.heightProperty().divide(5));
        ivStartServer.fitHeightProperty().bind(gpGameTile.heightProperty().divide(5));
        ivConnectServer.fitHeightProperty().bind(gpGameTile.heightProperty().divide(5));
        ivRunGame.fitWidthProperty().bind(gpGameTile.heightProperty().divide(5));
        ivDownloadGame.fitWidthProperty().bind(gpGameTile.heightProperty().divide(5));
        ivOpenExplorer.fitWidthProperty().bind(gpGameTile.heightProperty().divide(5));
        ivStartServer.fitWidthProperty().bind(gpGameTile.heightProperty().divide(5));
        ivConnectServer.fitWidthProperty().bind(gpGameTile.heightProperty().divide(5));
        //Mouse over effect for buttons
        ivRunGame.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivStartServer.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivConnectServer.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivOpenExplorer.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivRunGame.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivOpenExplorer.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivConnectServer.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivStartServer.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        //Grey Serverstart and Serverconnect if not available
        if(!game.isConnectDirect())
            ivConnectServer.setImage(new Image(ClassLoader.getSystemResource("serverconnect_md.png").toString(), true));
        if(!game.isOpenServer())
            ivStartServer.setImage(new Image(ClassLoader.getSystemResource("serverplay_md.png").toString(), true));

    }

    private void setDownloadbarVisibility(Boolean newValue) {
        if(!newValue){
            spDownloadGame.setVisible(false);
            lblDownloadSpeed.setVisible(false);
            if(ivDownloadGame.hoverProperty().get())
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("serverdownload_mo.png").toString(), true));
            else
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("serverdownload.png").toString(), true));
        } else {
            if(ivDownloadGame.hoverProperty().get())
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true));
            else
                ivDownloadGame.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
        }
    }

    private void mouseEntered(MouseEvent event){
        if((event.getTarget().equals(ivConnectServer) && game.isConnectDirect()) || (event.getTarget().equals(ivStartServer) && game.isOpenServer())
                || event.getTarget().equals(ivRunGame) || event.getTarget().equals(ivDownloadGame) || event.getTarget().equals(ivOpenExplorer)) {
            ImageView imageView = (ImageView)event.getTarget();
            if(imageView.equals(ivRunGame) && !gameStatus.running.get())
                imageView.setImage(new Image(ClassLoader.getSystemResource("play_mo.png").toString(), true));
            else if(imageView.equals(ivRunGame) && gameStatus.running.get())
                imageView.setImage(new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && !gameStatus.downloading.get() && !gameStatus.unzipping.get())
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverdownload_mo.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && (gameStatus.downloading.get() || gameStatus.unzipping.get()))
                imageView.setImage(new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true));
            else if(imageView.equals(ivOpenExplorer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("folder_mo.png").toString(), true));
            else if(imageView.equals(ivConnectServer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverconnect_mo.png").toString(), true));
            else if(imageView.equals(ivStartServer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverplay_mo.png").toString(), true));
        }
    }

    private void mouseExited(MouseEvent event){
        if((event.getTarget().equals(ivConnectServer) && game.isConnectDirect()) || (event.getTarget().equals(ivStartServer) && game.isOpenServer())
                || event.getTarget().equals(ivRunGame) || event.getTarget().equals(ivDownloadGame) || event.getTarget().equals(ivOpenExplorer)) {
            ImageView imageView = (ImageView)event.getTarget();
            if(imageView.equals(ivRunGame) && !gameStatus.running.get())
                imageView.setImage(new Image(ClassLoader.getSystemResource("play.png").toString(), true));
            else if(imageView.equals(ivRunGame) && gameStatus.running.get())
                imageView.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && !gameStatus.downloading.get() && !gameStatus.unzipping.get())
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverdownload.png").toString(), true));
            else if(imageView.equals(ivDownloadGame) && (gameStatus.downloading.get() || gameStatus.unzipping.get()))
                imageView.setImage(new Image(ClassLoader.getSystemResource("close.png").toString(), true));
            else if(imageView.equals(ivOpenExplorer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("folder.png").toString(), true));
            else if(imageView.equals(ivConnectServer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverconnect.png").toString(), true));
            else if(imageView.equals(ivStartServer))
                imageView.setImage(new Image(ClassLoader.getSystemResource("serverplay.png").toString(), true));
        }
    }

}
