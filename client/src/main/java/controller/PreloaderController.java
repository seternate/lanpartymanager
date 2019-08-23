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

public class PreloaderController extends Controller{

    @FXML
    private Label lblStatus;
    @FXML
    private ImageView closeButton;
    private int cycleDuration = 3000;
    private Animation animation;


    @FXML
    private void initialize(){
        animation = new Transition() {
            {
                setCycleDuration(Duration.millis(cycleDuration));
                setCycleCount(Animation.INDEFINITE);
                play();
            }

            protected void interpolate(double frac) {
                //Calculate number between 0 and 3
                int n = Math.round(3 * (float)frac);
                //Show status text for waiting connection
                if(getClient().getStatus().isConnected())
                    lblStatus.setText("Loading " + "...".substring(0, n));
                else
                    lblStatus.setText("Waiting for server " + "...".substring(0, n));
            }
        };
    }

    @FXML
    private void closeButtonClicked(){
        Platform.exit();
    }

    @FXML
    private void closeButtonMouseEntered(){
        Image closeMouseOver = new Image(ClassLoader.getSystemResource("close_mo.png").toString(), true);
        closeButton.setImage(closeMouseOver);
    }

    @FXML
    private void closeButtonMouseExited(){
        Image closeMouseOver = new Image(ClassLoader.getSystemResource("close_white.png").toString(), true);
        closeButton.setImage(closeMouseOver);
    }

    @Override
    public void shutdown(){
        animation.stop();
    }

    public int getCycleDuration(){
        return cycleDuration;
    }

}
