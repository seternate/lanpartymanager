package controller;

import entities.game.Game;
import javafx.scene.image.Image;
import org.apache.log4j.Logger;

import java.io.File;

public abstract class ControllerHelper {
    private static Logger log = Logger.getLogger(ControllerHelper.class);


    static Image getIcon(Game game){
        File iconpath = new File(ApplicationManager.getGamepath() + "/images");
        if (iconpath.listFiles() != null) {
            for (File icon : iconpath.listFiles()) {
                int index = icon.getName().lastIndexOf(".");
                if (icon.getName().substring(0, index).equals(game.getName() + "_icon")) {
                    log.info("Local icon of '" + game + "' found.");
                    return new Image("file:" + icon.getAbsolutePath(), true);
                }
            }
        }
        return new Image(ClassLoader.getSystemResource("dummyicon.png").toString(), true);
    }

}
