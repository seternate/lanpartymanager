package stages;

import controller.ApplicationManager;
import controller.MainController;
import entities.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

/**
 * MainStage class for the main window of the application.
 */
public class MainStage extends Stage {
    //Controller class of the MainStage
    private MainController controller;

    public MainStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("main.fxml"));
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
        setTitle("Lanpartymanager");
        controller = loader.getController();
        //MinHeight and MinWidth for the window
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
    /**
     * Called if any new game received from the server and reloads the gametiles in the main window.
     */
    public void updateRoot(){
        controller.updateGamePane();
        setWidth(getWidth()+1);
    }
    /**
     *
     * @return Game of the gametile, which is currently focused in the main window.
     */
    public Game getFocusedGame(){
        return controller.focusedGame;
    }
    /**
     * Hides all windows if main stage is closed, so the application terminates correctly.
     */
    @Override
    public void hide() {
        super.hide();
        ApplicationManager.closeAllMainStages();
    }
}
