package stages;

import javafx.stage.Screen;

import main.LanClient;
import org.apache.log4j.Logger;

import java.util.prefs.Preferences;

public class MainStage extends Stage {

    public MainStage(){
        setTitle("Lanpartymanager" + " (" + LanClient.client.getUser().getIpAddress() + ")");
        setMinWidth(600);
        setMinHeight(400);
        Preferences pref = Preferences.userRoot().node("MainStage");
        double x = pref.getDouble("win_pos_x", (Screen.getPrimary().getVisualBounds().getWidth() - getWidth()) / 2);
        double y = pref.getDouble("win_pos_y", (Screen.getPrimary().getVisualBounds().getHeight() - getHeight()) / 2);
        double width = pref.getDouble("win_width", getMinWidth());
        double height = pref.getDouble("win_height", getMinHeight());
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.setOnCloseRequest(event -> {
            Preferences preferences = Preferences.userRoot().node("MainStage");
            preferences.putDouble("win_pos_x", getX());
            preferences.putDouble("win_pos_y", getY());
            preferences.putDouble("win_width", getWidth());
            preferences.putDouble("win_height", getHeight());
        });
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(MainStage.class);
    }

    @Override
    public String getFXML() {
        return "main.fxml";
    }

}
