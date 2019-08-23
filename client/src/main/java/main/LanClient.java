package main;

import client.LANClient;
import com.esotericsoftware.minlog.Log;
import controller.ApplicationManager;
import helper.kryo.KryoLogging;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * {@code LanClient} creates the {@link LANClient} and launches the {@code FXGui}.
 *
 * @author Levin Jeck
 * @version 2.0
 * @see Application
 * @since 1.0
 */
public class LanClient extends Application {
    public static LANClient client;


    /**
     * {@code main} method of the {@code LanClient}.
     * <p>
     *     Starts the {@link LANClient} and launches the {@code FXGui}.
     * </p>
     *
     * @param args command-line arguments
     * @since 1.0
     */
    public static void main(String[] args) {
        //Disable all logging for KryoNet
        Log.setLogger(new KryoLogging());
        //Create the LANClient
        client = new LANClient();
        //Launch the GUI
        launch(args);
    }

    /**
     * Starting method for {@link Application}.
     *
     * @param primaryStage stage opened by calling {@link #launch(String...)}
     * @since 1.0
     */
    @Override
    public void start(Stage primaryStage) {
        ApplicationManager.start();
    }

}
