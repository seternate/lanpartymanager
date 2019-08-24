package controller;

import entities.game.Game;
import javafx.scene.image.Image;
import org.apache.log4j.Logger;

import java.io.File;

public abstract class ControllerHelper {

    static Image getIcon(Game game){
        File iconpath = new File(ApplicationManager.getGamepath() + "/images");
        if (iconpath.listFiles() != null) {
            for (File icon : iconpath.listFiles()) {
                int index = icon.getName().lastIndexOf(".");
                if (icon.getName().substring(0, index).equals(game.getName() + "_icon")) {
                    return new Image("file:" + icon.getAbsolutePath(), true);
                }
            }
        }
        return new Image(ClassLoader.getSystemResource("dummyicon.png").toString(), true);
    }

    static Image getCover(Game game) {
        File coverpath = new File(ApplicationManager.getGamepath() + "/images");
        if(coverpath.listFiles() != null){
            for(File cover : coverpath.listFiles()){
                int index = cover.getName().lastIndexOf(".");
                if(cover.getName().substring(0, index).equals(game.getName())) {
                    return new Image("file:" + cover.getAbsolutePath(), true);
                }
            }
        }
        if(!game.getCoverUrl().isEmpty()) {
            return new Image(game.getCoverUrl(), true);
        }
        return new Image(ClassLoader.getSystemResource("dummycover.jpg").toString(),true);
    }

}
