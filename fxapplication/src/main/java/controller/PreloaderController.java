package controller;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class PreloaderController {
    private Animation animation;

    @FXML
    private Label lblStatus;

    @FXML
    private void initialize(){
        animation = animateText(lblStatus);
        animation.play();
    }

    public void stopAnimations(){
        animation.stop();
    }

    private Animation animateText(Label label){
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(3000));
            }
            protected void interpolate(double frac) {
                int n = Math.round(3 * (float)frac);
                label.setText("Waiting for client-application " + "...".substring(0, n));
            }
        };
        animation.setOnFinished((ActionEvent event) -> animation.play());
        return animation;
    }
}
