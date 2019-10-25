package stages;

import controller.UsersController;
import javafx.stage.Screen;
import org.apache.log4j.Logger;

import java.util.prefs.Preferences;

public class UsersStage extends Stage {

    public UsersStage(){
        setTitle("Users");
        setMinWidth(250);
        setMaxWidth(350);
        setMinHeight(400);

        Preferences pref = Preferences.userRoot().node("UserStage");
        double x = pref.getDouble("win_pos_x", (Screen.getPrimary().getVisualBounds().getWidth() - getWidth()) / 2);
        double y = pref.getDouble("win_pos_y", (Screen.getPrimary().getVisualBounds().getHeight() - getHeight()) / 2);
        double width = pref.getDouble("win_width", getMinWidth());
        double height = pref.getDouble("win_height", getMinHeight());
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        this.setOnCloseRequest(event -> {
            Preferences preferences = Preferences.userRoot().node("UserStage");
            preferences.putDouble("win_pos_x", getX());
            preferences.putDouble("win_pos_y", getY());
            preferences.putDouble("win_width", getWidth());
            preferences.putDouble("win_height", getHeight());
        });
    }

    public void update(){
        ((UsersController)getController()).update();
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(UsersStage.class);
    }

    @Override
    public String getFXML() {
        return "users.fxml";
    }

}
