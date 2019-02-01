package clientInterface;

import controller.ApplicationManager;
import deserialize.User;
import entities.ServerStatus;
import javafx.application.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class Client extends Thread {
    private FXDataClient client;
    private volatile ServerStatus status;
    private volatile User user;

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
            sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Close PreloaderStage after one successful connection to the Client-Application.
        while(status == null) {
            try {
                status = client.getStatus().execute().body();
                user = client.getUser().execute().body();
                sleep(50);
                Platform.runLater(ApplicationManager::openLoginStage);
            } catch (Exception e) {
                System.err.println("No client-application found.");
            }
        }

        //Update all fields while any stage is open.
        while(ApplicationManager.isRunning()){
            try {
                status = client.getStatus().execute().body();
                user = client.getUser().execute().body();
                sleep(50);
            } catch (Exception e) {
                System.err.println("Client-application connection problems.");
            }
        }
    }

    public String getUsername(){
        return this.user.getUsername();
    }

    public String getGamepath(){
        return user.getGamepath();
    }
}
