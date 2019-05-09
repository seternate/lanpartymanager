package clientInterface;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public final class GameStatusProperty {
    public SimpleBooleanProperty playable, local, version, update, downloading, unzipping, running;
    public SimpleDoubleProperty downloadProgress, unzipProgress;
    public SimpleStringProperty downloadSpeed;


    public GameStatusProperty(){
        playable = new SimpleBooleanProperty(false);
        local = new SimpleBooleanProperty(true);
        version = new SimpleBooleanProperty(true);
        update = new SimpleBooleanProperty(false);
        downloading = new SimpleBooleanProperty(false);
        unzipping = new SimpleBooleanProperty(false);
        running = new SimpleBooleanProperty(false);
        downloadProgress = new SimpleDoubleProperty(0.);
        unzipProgress = new SimpleDoubleProperty(0.);
        downloadSpeed = new SimpleStringProperty("");
    }

}
