package controller;

import entities.Game;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
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

public class GameOverlayController {
    @FXML
    private ImageView ivRunGame, ivDownloadGame, ivOpenExplorer, ivStartServer, ivConnectServer;
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
        lblGamename.setText(game.getName());
        lblVersion.setText(game.getVersionServer());
        lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, pbDownload.getHeight()*0.5));

        ivRunGame.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivDownloadGame.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivOpenExplorer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivStartServer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));
        ivConnectServer.fitHeightProperty().bind(gameTileImage.fitHeightProperty().divide(7));

        gameTileImage.fitHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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
            }
        });

        hbDownloadGame.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                VBox.setMargin(spDownloadGame, new Insets(newValue.doubleValue()*0.1, 0, newValue.doubleValue()*0.1, newValue.doubleValue()*0.25));
            }
        });

        pbDownload.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                lblDownloadbar.setFont(Font.font("System", FontWeight.NORMAL, newValue.doubleValue()*0.5));
            }
        });

        ivRunGame.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ApplicationManager.startGame(game);
                event.consume();
            }
        });

        ivDownloadGame.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ApplicationManager.downloadGame(game);
            }
        });
    }
}
