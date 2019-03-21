package clientInterface;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public final class GameStatusProperty {
    public SimpleBooleanProperty playable, download, version, update, downloading, unzipping;
    public SimpleDoubleProperty downloadProgress, unzipProgress;

    public GameStatusProperty(){
        playable = new SimpleBooleanProperty(false);
        download = new SimpleBooleanProperty(false);
        version = new SimpleBooleanProperty(true);
        update = new SimpleBooleanProperty(false);
        downloading = new SimpleBooleanProperty(false);
        unzipping = new SimpleBooleanProperty(false);
        downloadProgress = new SimpleDoubleProperty(0);
        unzipProgress = new SimpleDoubleProperty(0);
    }

}
