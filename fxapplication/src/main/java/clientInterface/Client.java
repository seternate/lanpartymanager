package clientInterface;

import entities.ServerStatus;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.awt.event.ActionListener;
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
        status = new ServerStatus();
        user = new User();
    }

    @Override
    public void run() {
        try {
            status = client.getStatus().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public String getUsername(){
        //Todo
        return String.valueOf(status.isConnected());
    }

    public String getGamepath(){
        //Todo
        return "gamepath";
    }
}
