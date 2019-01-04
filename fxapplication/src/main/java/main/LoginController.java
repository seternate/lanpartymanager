package main;

import entities.Status;
import entities.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.ConnectException;

import static java.lang.Thread.sleep;

public class LoginController extends Application {
    private Retrofit retrofit;
    private FXDataService client;
    private Stage stage;


    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnFinish;


    public LoginController() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/fx/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        client = retrofit.create(FXDataService.class);
    }

    @Override
    public void init(){
        lookForClient();
        updateStatus();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        User login = getLogin();
        this.stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Preloader.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("Preloader.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Lanpartymanager - Start");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("icon.png").toExternalForm()));
        txtfieldUsername.setText(login.getName());
        txtfieldGamepath.setText(login.getGamepath());
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.exit(0);
    }

    private void lookForClient(){
        try {
            client.getStatus().execute();
        } catch (ConnectException e) {
            System.err.println("No client running.");
            System.exit(-15);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-16);
        }
    }

    private void updateStatus(){
        Call<Status> callStatus = client.getStatus();
        callStatus.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                Status status = response.body();
                if(status.serverConnection)
                    Platform.runLater(() -> lblStatus.setText("Connected to server: " + status.serverIP));
                else
                    Platform.runLater(() -> lblStatus.setText("No server found."));
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateStatus();
            }
            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                System.err.println("No client running.");
                System.exit(-19);
            }
        });
    }

    private User getLogin(){
        User login = null;
        try {
            login = client.getLogin().execute().body();
        } catch (ConnectException e) {
            e.printStackTrace();
            System.err.println("No client running.");
            System.exit(-18);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No logindata received.");
            System.exit(-17);
        }
        return login;
    }

    @FXML
    public void openInterface(ActionEvent event){

    }
    /*
    @Override
    public void stop() throws Exception {
        super.stop();
        running = false;
    }

    private void clientStatus(){
        Call<ResponseBody> call = service.getStatus();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseText = response.body().string();
                    if(responseText.contains("Client connected with Server")){
                        clientConnected = true;
                    }else{
                        clientConnected = false;
                    }
                    Platform.runLater(() -> lblStatus.setText(responseText));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(running) clientStatus();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                clientConnected = false;
                System.err.println("Client won't response to FXApp.");
                Platform.runLater(() -> lblStatus.setText("Waiting for Client ..."));
                if(running) clientStatus();
            }
        });
    }

    @FXML
    public void openInterface(ActionEvent event){
        if(clientConnected){
            running = false;
            updateProperties();
            loadInterface();
        }else{
            lblStatus.setText("Client not connected to any Server.");
        }
    }

    private void loadInterface(){
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Interface.fxml"));
        loader.setController(new InterfaceController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("Interface.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Lanpartymanager");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("icon.png").toExternalForm()));
        stage.setResizable(false);
        this.stage.hide();
        stage.show();
    }

    private void updateProperties(){
        PropertiesHelper.setUserName(txtfieldUsername.getText());
        PropertiesHelper.setGamePath(txtfieldGamepath.getText());
        Call<Void> call = service.login();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) { }
            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }
    */
}
