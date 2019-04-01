package controller;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class PreloaderController {
    @FXML
    private Label lblStatus;

    @FXML
    private void initialize(){
        animateText(lblStatus);
    }

    private void animateText(Label label){
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(3000));
            }
            protected void interpolate(double frac) {
                int n = Math.round(3 * (float)frac);
                if(ApplicationManager.getServerStatus() == null)
                    label.setText("Waiting for client " + "...".substring(0, n));
                else if(!ApplicationManager.getServerStatus().isConnected())
                    label.setText("Waiting for server " + "...".substring(0, n));
            }
        };
        animation.setOnFinished((ActionEvent event) -> {
            if(ApplicationManager.isPreloader())
                animation.play();
            if(ApplicationManager.getServerStatus().isConnected()) {
                ApplicationManager.openLoginStage();
                animation.stop();
            }
        });
        animation.play();
    }
}
