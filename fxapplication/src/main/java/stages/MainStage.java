package stages;

import controller.ApplicationManager;
import controller.MainController;
import entities.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

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
    }
    /**
     * Called if any new game received from the server and reloads the gametiles in the main window.
     */
    public void updateRoot(){
        controller.updateGamePane();
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
