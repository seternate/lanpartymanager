package stages;

import controller.ApplicationManager;
import org.apache.log4j.Logger;

public class SettingsStage extends Stage {

    public SettingsStage(){
        super();
        setTitle("Settings");
        setResizable(false);
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(SettingsStage.class);
    }

    @Override
    public String getFXML() {
        return "login.fxml";
    }

}
