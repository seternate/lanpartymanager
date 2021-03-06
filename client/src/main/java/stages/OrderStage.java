package stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

public class OrderStage extends Stage {

    public OrderStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("food.fxml"));
        try {
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream icon = ClassLoader.getSystemResourceAsStream("icon.png");
        if (icon != null) {
            getIcons().add(new Image(icon));
        }
        setTitle("Food ordering");
        //MinHeight and MinWidth for the window
        setMinWidth(330);
        setMaxWidth(500);
        setMinHeight(250);

        Preferences pref = Preferences.userRoot().node("OrderStage");
        double x = pref.getDouble("win_pos_x", (Screen.getPrimary().getVisualBounds().getWidth() - getWidth()) / 2);
        double y = pref.getDouble("win_pos_y", (Screen.getPrimary().getVisualBounds().getHeight() - getHeight()) / 2);
        double width = pref.getDouble("win_width", getMinWidth());
        double height = pref.getDouble("win_height", getMinHeight());
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        this.setOnCloseRequest(event -> {
            Preferences preferences = Preferences.userRoot().node("OrderStage");
            preferences.putDouble("win_pos_x", getX());
            preferences.putDouble("win_pos_y", getY());
            preferences.putDouble("win_width", getWidth());
            preferences.putDouble("win_height", getHeight());
        });
    }

}
