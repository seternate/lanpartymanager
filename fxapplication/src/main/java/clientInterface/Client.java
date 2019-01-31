package clientInterface;

import com.sun.javafx.stage.StageHelper;
import controller.ApplicationManager;
import entities.ServerStatus;
import entities.User;
import javafx.application.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class Client extends Thread {
    private FXDataClient client;
    private ServerStatus status;
    private User user;

    public Client(){
        start();
    }

    @Override
    public void run() {
        //Client initiation
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/fx/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        client = retrofit.create(FXDataClient.class);
        status = null;
        user = new User();

        //Ensure the preloader is shown at least 3 seconds.
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Close PreloaderStage after one successful connection to the Client-Application.
        while(status == null) {
            try {
                status = client.getStatus().execute().body();
                sleep(50);
                Platform.runLater(ApplicationManager::openLoginStage);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("No client-application found.");
            }
        }

        //Update all fields while any stage is open.
        while(ApplicationManager.isRunning()){
            try {
                sleep(50);
                status = client.getStatus().execute().body();
            } catch (Exception e) {
                System.err.println("Client-application connection problems.");
            }
        }
    }
}
