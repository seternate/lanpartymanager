package clientInterface;

import controller.ApplicationManager;
import entities.ServerStatus;
import entities.User;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class Client extends Thread {
    private FXDataClient client;
    private ServerStatus status;
    private User user;

    public Client(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/fx/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        client = retrofit.create(FXDataClient.class);
        status = null;
        user = new User();
        start();
    }

    @Override
    public void run() {
        //Close PreloaderStage after one successful connection to the Client-Application.
        while(status == null) {
            try {
                status = client.getStatus().execute().body();
                ApplicationManager.notifyPreloader();
            } catch (Exception e) {
                System.err.println("Cannot initialize.");
            }
        }

        //Update all fields.
        while(true){
            try {
                status = client.getStatus().execute().body();
            } catch (IOException e) {

            }
        }
    }
}
