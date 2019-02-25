package controller;

import entities.Game;
import entities.GameStatusProperty;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Locale;

public class GameOverlayController {
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

    ImageView gameTileImage;
    Game game;

    public GameOverlayController(ImageView gameTileImage, Game game){
        this.game = game;
        this.gameTileImage = gameTileImage;
    }

    @FXML
    private void initialize(){
        GameStatusProperty gameStatus = ApplicationManager.getGamestatusProperty();

        lblGamename.setText(game.getName());
        lblVersion.setText(game.getVersionServer());
        lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, pbDownload.getHeight()*0.5));

        spDownloadGame.setVisible(false);
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

        ivRunGame.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivDownloadGame.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivOpenExplorer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivStartServer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivConnectServer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));

        gameTileImage.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            double fontSize = newValue.doubleValue()/12.0;
            lblGamename.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            lblVersion.setFont(Font.font("System", FontWeight.BOLD, fontSize));

            double leftSpace = newValue.doubleValue() - lblGamename.getFont().getSize() - lblVersion.getFont().getSize() - 5.75*ivRunGame.fitHeightProperty().get();
            Insets margin = new Insets(leftSpace/6.0, newValue.doubleValue()/30.0, 0, newValue.doubleValue()/30.0);

            VBox.setMargin(lblGamename, new Insets(0, 0, 0, margin.getLeft()));
            VBox.setMargin(lblVersion, new Insets(0, 0, 0, margin.getLeft()));
            VBox.setMargin(ivRunGame, margin);
            VBox.setMargin(hbDownloadGame, margin);
            VBox.setMargin(ivOpenExplorer, margin);
            VBox.setMargin(ivStartServer, margin);
            VBox.setMargin(ivConnectServer, new Insets(margin.getTop(), 0, margin.getTop(), margin.getLeft()));
        });

        hbDownloadGame.heightProperty().addListener((observable, oldValue, newValue) -> VBox.setMargin(spDownloadGame, new Insets(newValue.doubleValue()*0.1, 0, newValue.doubleValue()*0.1, newValue.doubleValue()*0.25)));

        pbDownload.heightProperty().addListener((observable, oldValue, newValue) -> lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, newValue.doubleValue()*0.5)));

        ivRunGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ApplicationManager.startGame(game);
            event.consume();
        });

        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ApplicationManager.downloadGame(game);
            event.consume();
        });

        ivOpenExplorer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ApplicationManager.openExplorer(game);
            event.consume();
        });

        ivConnectServer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ApplicationManager.openServerList(game);
            event.consume();
        });

        ivStartServer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ApplicationManager.openServerStartup(game);
            event.consume();
        });
    }

    private void setDownloadbarVisibility(Boolean newValue) {
        if(newValue == false){
            spDownloadGame.setVisible(false);
        }
    }
}
