package controller;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.log4j.Logger;

public class PreloaderController {
    private static Logger log = Logger.getLogger(PreloaderController.class);


    @FXML
    private Label lblStatus;
    @FXML
    private ImageView closeButton;
    private Animation animation;


    @FXML
    private void initialize(){
        int cycleDuration = 3000;

        animation = new Transition() {
            {
                setCycleDuration(Duration.millis(cycleDuration));
                log.info("Animation cycle duration: " + cycleDuration);
                setCycleCount(Animation.INDEFINITE);
                play();
                log.info("Animation started.");
            }

            protected void interpolate(double frac) {
                //Calculate number between 0 and 3
                int n = Math.round(3 * (float)frac);
                //Show status text for waiting connection
                if(ApplicationManager.getServerStatus() == null)
                    lblStatus.setText("Waiting for client " + "...".substring(0, n));
                else if(!ApplicationManager.getServerStatus().isConnected())
                    lblStatus.setText("Waiting for server " + "...".substring(0, n));
            }
        };
        log.info("Started animation.");
    }

    @FXML
    private void closeButtonClicked(){
        log.info("User clicked the close button.");
        Platform.exit();
    }

    @FXML
    private void closeButtonMouseEntered(){
        Image closeMouseOver = new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true);
        closeButton.setImage(closeMouseOver);
        log.info("Mouse entered close button.");
    }

    @FXML
    private void closeButtonMouseExited(){
        Image closeMouseOver = new Image(ClassLoader.getSystemResource("close_white.png").toString(), true);
        closeButton.setImage(closeMouseOver);
        log.info("Mouse exited close button.");
    }

    public void shutdown(){
        animation.stop();
        log.info("Animation stopped.");
    }

}
