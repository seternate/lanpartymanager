package controller;

import entities.Game;
import entities.GameStatusProperty;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Locale;

/**
 * Controller class for the GameOverlay/Gametile of the games in the main stage.
 */
class GameOverlayController {
    /**
     * FXML GUI-Elements
     */
    @FXML
    private ImageView ivRunGame, ivDownloadGame, ivOpenExplorer, ivConnectServer, ivStartServer;
    @FXML
    private HBox hbDownloadGame;
    @FXML
    private Label lblGamename, lblVersion, lblDownloadbar;
    @FXML
    private ProgressBar pbDownload;
    @FXML
    private StackPane spDownloadGame;
    /**
     * GameOverlay background data
     */
    private ImageView gameTileImage;
    private Game game;
    /**
     * Constructs the GameOverlayController.
     *
     * @param gameTileImage - Cover image of the game shown in the gametile.
     * @param game - Game which is represented by the gametile.
     */
    GameOverlayController(ImageView gameTileImage, Game game){
        this.game = game;
        this.gameTileImage = gameTileImage;
    }
    /**
     * Initialization of the GameOverlay.
     */
    @FXML
    private void initialize(){
        //Hiding the downloadbar
        spDownloadGame.setVisible(false);
        lblGamename.setText(game.getName());
        lblVersion.setText(game.getVersionServer());
        //Setting progressbar label fontstyle
        lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, pbDownload.getHeight()*0.5));

        //Getting the gamestatus variable to listen to for listener implementation for downloading and unzipping.
        GameStatusProperty gameStatus = ApplicationManager.getGamestatusProperty();
        //Dynamically setting progressbar margin to the download image top and bottom
        hbDownloadGame.heightProperty().addListener((observable, oldValue, newValue) -> VBox.setMargin(spDownloadGame, new Insets(newValue.doubleValue()*0.1, 0, newValue.doubleValue()*0.1, newValue.doubleValue()*0.25)));
        //Dynamically updating progressbar label fontsize
        pbDownload.heightProperty().addListener((observable, oldValue, newValue) -> lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, newValue.doubleValue()*0.5)));
        /*
            Game downloading status listener to show/hide the progressbar and to show the download progress.
         */
        gameStatus.downloading.addListener((observable, oldValue, newValue) -> setDownloadbarVisibility(newValue));
        gameStatus.downloadProgress.addListener((observable, oldValue, newValue) -> {
            if(ApplicationManager.getFocusedGame().equals(game) && newValue.doubleValue() > 0) {
                spDownloadGame.setVisible(true);
                Platform.runLater(() -> {
                    pbDownload.setProgress(newValue.doubleValue());
                    lblDownloadbar.setText(String.format(Locale.ENGLISH,"%.02f %%", newValue.doubleValue() * 100));
                });
            }
        });
        /*
            Game downloading status listener to show/hide the progressbar and to show the download progress.
         */
        gameStatus.unzipping.addListener((observable, oldValue, newValue) -> setDownloadbarVisibility(newValue));
        gameStatus.unzipProgress.addListener((observable, oldValue, newValue) -> {
            if(ApplicationManager.getFocusedGame().equals(game) && newValue.doubleValue() > 0) {
                spDownloadGame.setVisible(true);
                Platform.runLater(() -> {
                    pbDownload.setProgress(newValue.doubleValue());
                    lblDownloadbar.setText(String.format(Locale.ENGLISH, "Unzip: %.02f %%", newValue.doubleValue() * 100));
                });
            }
        });
        /*
            Add all required tooltips to the buttons.
         */
        Tooltip.install(lblVersion, new Tooltip("Version"));
        Tooltip.install(ivRunGame, new Tooltip("Run game"));
        Tooltip.install(ivDownloadGame, new Tooltip("Download game"));
        Tooltip.install(ivOpenExplorer, new Tooltip("Open game folder in explorer"));
        Tooltip.install(ivConnectServer, new Tooltip("Connect to an open server"));
        Tooltip.install(ivStartServer, new Tooltip("Start a new server"));
        /*
            Dynamically resizing image buttons to the gametile image.
         */
        ivRunGame.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivDownloadGame.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivOpenExplorer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivStartServer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivConnectServer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        /*
            All dynamic margin resizing and fontsize resizing.
         */
        gameTileImage.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            //Font resizing
            double fontSize = newValue.doubleValue()/12.0;
            lblGamename.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            lblVersion.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            //Margin calculation with left space
            double leftSpace = newValue.doubleValue() - lblGamename.getFont().getSize() - lblVersion.getFont().getSize() - 5.75*ivRunGame.fitHeightProperty().get();
            Insets margin = new Insets(leftSpace/6.0, newValue.doubleValue()/30.0, 0, newValue.doubleValue()/30.0);
            //Setting margins
            VBox.setMargin(lblGamename, new Insets(0, 0, 0, margin.getLeft()));
            VBox.setMargin(lblVersion, new Insets(0, 0, 0, margin.getLeft()));
            VBox.setMargin(ivRunGame, margin);
            VBox.setMargin(hbDownloadGame, margin);
            VBox.setMargin(ivOpenExplorer, margin);
            VBox.setMargin(ivStartServer, margin);
            VBox.setMargin(ivConnectServer, new Insets(margin.getTop(), 0, margin.getTop(), margin.getLeft()));
        });
        /*
            MouseClick event handler for the image buttons
         */
        ivRunGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.startGame(game);
            event.consume();
        });
        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.downloadGame(game);
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
        /*
            Mouse hover animation for the image buttons. If the button has functionality then it is animated.
         */
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

    }
    /**
     * Hiding progressbar visibility if download or unzip status changing to false.
     *
     * @param newValue - download or unzip status
     */
    private void setDownloadbarVisibility(Boolean newValue) {
        if(!newValue){
            spDownloadGame.setVisible(false);
        }
    }
    /**
     * Starting image button animation.
     *
     * @param event - MouseEvent mouseentered for the image buttons.
     */
    private void mouseEntered(MouseEvent event){
        if((event.getTarget().equals(ivConnectServer) && game.isConnectDirect()) || (event.getTarget().equals(ivStartServer) && game.isOpenServer())
                || event.getTarget().equals(ivRunGame) || event.getTarget().equals(ivDownloadGame) || event.getTarget().equals(ivOpenExplorer)) {
            ((ImageView) event.getTarget()).setStyle("-fx-effect: dropshadow(gaussian, gray, 10, 0.05, 0, 3);");
            ((ImageView) event.getTarget()).fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(6.75));
        }
    }
    /**
     * Ending image button animation.
     *
     * @param event - MouseEvent mouseexited for the image buttons.
     */
    private void mouseExited(MouseEvent event){
        if((event.getTarget().equals(ivConnectServer) && game.isConnectDirect()) || (event.getTarget().equals(ivStartServer) && game.isOpenServer())
                || event.getTarget().equals(ivRunGame) || event.getTarget().equals(ivDownloadGame) || event.getTarget().equals(ivOpenExplorer)) {
            ((ImageView)event.getTarget()).setStyle("-fx-effect: null;");
            ((ImageView)event.getTarget()).fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        }
    }
}
