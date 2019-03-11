package stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

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
        setMinWidth(260);
        setMinHeight(250);
    }

}
