package guielements;

import controller.Controller;
import controller.GameOverlayController;
import controller.NotificationController;
import entities.game.Game;
import entities.user.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;
import stages.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Notification extends Popup {

    private VBox root;
    private Controller controller;

    public Notification(Stage owner, User user, Game game){
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(getFXML()));
        NotificationController notificationController = new NotificationController(user, game);
        controller = notificationController;
        loader.setController(notificationController);
        try {
            root = loader.load();
            controller = loader.getController();
            setHideOnEscape(true);
            getContent().add(root);
            setX(Screen.getPrimary().getVisualBounds().getWidth() - root.getPrefWidth() - 10);
            setY(Screen.getPrimary().getVisualBounds().getHeight() - root.getPrefHeight() - 10);
            show(owner);
            setTimer(5000);
            getScene().getStylesheets().add("popupstyle.css");
        } catch (IOException e) {
            getLogger().fatal("Could not loaded " + getFXML());
            getLogger().debug("Could not loaded " + getFXML(), e);
        }
    }

    public String getFXML() {
        return "notification.fxml";
    }

    public Logger getLogger() {
        return Logger.getLogger(Notification.class);
    }

    private void setTimer(int start){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> hide());
            }
        }, start);
    }

}
