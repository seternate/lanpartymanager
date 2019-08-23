package stages;

import controller.ApplicationManager;
import controller.PreloaderController;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

public class PreloaderStage extends Stage {

    public PreloaderStage(){
        initStyle(StageStyle.UNDECORATED);
        setResizable(false);
        setTitle("Lanpartymanager - Splash");
        setOnHiding(e -> getController().shutdown());
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(PreloaderStage.class);
    }

    @Override
    public String getFXML() {
        return "preloader.fxml";
    }

    @Override
    public void hide() {
        super.hide();
        if(!ApplicationManager.isRunning()){
            getLogger().fatal("Illegal close of the PreloaderStage.");
            System.exit(-5);
        }
    }

    public int getAnimationCycleDuration(){
        return ((PreloaderController)getController()).getCycleDuration();
    }

}
