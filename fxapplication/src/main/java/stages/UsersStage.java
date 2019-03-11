package stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * UserStage for showing the userlist of all connected users to the server except yourself showing from the main stage
 * button at the bottom.
 */
public class UsersStage extends Stage {
    public UsersStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("users.fxml"));
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
        setTitle("Users");
        setMinWidth(220);
        setMaxWidth(400);
        setMinHeight(250);
    }
}
