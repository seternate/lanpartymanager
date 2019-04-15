package main;

import client.LANClient;
import com.esotericsoftware.minlog.Log;
import controller.ApplicationManager;
import helper.kryo.NoKryoLogging;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="springboot")
public class LanClient extends Application {
    public static LANClient client;


    /**
     * Main-method of the LANClient app. Starts the LANClient and the Springbot-webapp.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        Log.setLogger(new NoKryoLogging());
        client = new LANClient();

        SpringApplication.run(LanClient.class, args);

        launch(args);
    }

    /**
     * Starting method for {@link Application}.
     *
     * @param primaryStage stage opened by calling {@link #launch(String...)}.
     */
    @Override
    public void start(Stage primaryStage) {
        ApplicationManager.start();
    }

}
