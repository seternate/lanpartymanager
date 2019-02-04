package clientInterface;

import controller.ApplicationManager;
import deserializer.User;
import entities.GameList;
import entities.ServerStatus;
import javafx.application.Platform;
import javafx.scene.control.Label;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class Client extends Thread {
    private FXDataClient client;
    private volatile ServerStatus status;
    private volatile User user;
    private volatile Label lblStatus;
    private volatile GameList games;

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
        lblStatus = null;

        //Ensure the preloader is shown at least 1.5 seconds.
        try {
            sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Close PreloaderStage after one successful connection to the Client-Application.
        while(status == null) {
            try {
                update();
                Platform.runLater(ApplicationManager::openLoginStage);
                sleep(50);
            } catch (Exception e) {
                System.err.println("No client-application found.");
            }
        }

        //Update all fields while any stage is open.
        while(ApplicationManager.isRunning()){
            try {
                update();
                updateStatusLabel();
                sleep(50);
            } catch (Exception e) {
                System.err.println("Client-application connection problems.");
            }
        }
    }

    public User getUser(){
        return user;
    }

    public ServerStatus getServerStatus(){
        return status;
    }

    public GameList getGames(){
        return games;
    }

    public void setServerStatusLabel(Label lblStatus){
        this.lblStatus = lblStatus;
    }

    public void sendUserData(String username, String gamepath){
        new Thread(() -> {
            User userdata = new User();
            userdata.setUsername(username);
            userdata.setGamepath(gamepath);
            try {
                client.sendUser(userdata).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void update() throws IOException {
        status = client.getStatus().execute().body();
        user = client.getUser().execute().body();
        games = client.getGames().execute().body();
    }

    private void updateStatusLabel(){
        if(lblStatus == null)
            return;
        Platform.runLater(() -> {
            if(status.isConnected())
                lblStatus.setText("Connected to server: " + status.getServerIP());
            else
                lblStatus.setText("Waiting for server connection.");
        });
    }
}
