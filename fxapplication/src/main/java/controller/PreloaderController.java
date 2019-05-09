package controller;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.apache.log4j.Logger;

public class PreloaderController {
    private static Logger log = Logger.getLogger(PreloaderController.class);


    @FXML
    private Label lblStatus;


    @FXML
    private void initialize(){
        log.info("Initializing PreloaderController.");
        animateText(lblStatus);
    }

    private void animateText(Label label){
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(3000));
            }
            protected void interpolate(double frac) {
                //Calculate number between 0 and 3
                int n = Math.round(3 * (float)frac);
                //Show status text for waiting connection
                if(ApplicationManager.getServerStatus() == null)
                    label.setText("Waiting for client " + "...".substring(0, n));
                else if(!ApplicationManager.getServerStatus().isConnected())
                    label.setText("Waiting for server " + "...".substring(0, n));
            }
        };
        //Actions on animation finish
        animation.setOnFinished((ActionEvent event) -> {
            //Replay if Preloader is open
            if(ApplicationManager.isPreloader())
                animation.play();
            //Stop animation if Preloader is closed
            if(ApplicationManager.getServerStatus().isConnected()) {
                ApplicationManager.openLoginStage();
                animation.stop();
                log.info("Stopped playing animation.");
            }
        });
        animation.play();
        log.info("Playing animation.");
    }

}
