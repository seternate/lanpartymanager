package main;

import entities.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

public class InterfaceController extends Application {

    Retrofit retrofit;
    DataService service;

    List<Game> gamelist;


    public InterfaceController() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        service = retrofit.create(DataService.class);
    }

    @Override
    public void init() throws Exception {
        Call<List<Game>> call = service.listGames();
        Boolean response = false;
        while(!response){
            try{
                response = service.isOnline().execute().body();
            }catch(IOException e){

            }
        }
        notifyPreloader(new Preloader.ProgressNotification(0.2));
        call.enqueue(new Callback<List<Game>>(){
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                gamelist = response.body();
                notifyPreloader(new Preloader.ProgressNotification(0.5));
                System.out.println("hallo");
            }
            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {

            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Interface.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("Interface.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
