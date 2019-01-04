package main;

import entities.Game;
import entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

public class InterfaceController{
/*
    private Retrofit retrofit;
    private FXDataService service;

    private ObservableList<Game> gamelist = FXCollections.observableArrayList();
    private ObservableList<User> userlist = FXCollections.observableArrayList();


    @FXML
    private Label lblStatus, lblVersion, lblGamename, lblAvailable;
    @FXML
    private ListView<Game> lvGames;


    public InterfaceController() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        service = retrofit.create(FXDataService.class);
        updateGamelist();
        //updateUserlist();
        updateStatus();
        initListview();
    }

    private void updateGamelist(){
        Call<List<Game>> callGame = service.getGamelist();
        callGame.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if(!equalList(gamelist, response.body())){
                    Platform.runLater(() -> gamelist.setAll(response.body()));
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateGamelist();
            }
            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                System.err.println("Error on gamelist request.");
                if(!lblStatus.getText().contains("Waiting"))
                    Platform.runLater(() -> lblStatus.setText("Error: Requesting gamelist."));
                updateGamelist();
            }
        });
    }

    private boolean equalList(ObservableList<Game> local, List<Game> remote){
        if(local.size() != remote.size()) return false;
        for(Game gameLocal : local){
            boolean same = false;
            for(Game gameRemote : remote){
                if(gameLocal.getName().equals(gameRemote.getName())){
                    same = true;
                    break;
                }
            }
            if(!same) return false;
        }
        return true;
    }

    /*
    private void updateUserlist(){
        Call<List<User>> callUser = service.getUserlist();
        callUser.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();
                if(!userlist.containsAll(users))
                    Platform.runLater(() -> userlist.setAll(users));
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateUserlist();
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.err.println("Error on userlist request.");
                if(!lblStatus.getText().contains("Waiting"))
                    Platform.runLater(() -> lblStatus.setText("Error: Requesting userlist."));
                updateUserlist();
            }
        });
    }


    private void updateStatus(){
        Call<ResponseBody> callStatus = service.getStatus();
        callStatus.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    String responseText = response.body().string();
                    Platform.runLater(() -> lblStatus.setText(responseText));
                }catch(IOException e){ }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateStatus();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.err.println("Client won't response to FXApp.");
                Platform.runLater(() -> lblStatus.setText("Waiting for Client ..."));
                updateStatus();
            }
        });
    }

    private void initListview(){
        Platform.runLater(() -> {
            gamelist.addListener((ListChangeListener<Game>)c -> {
                lvGames.setItems(gamelist);
                lvGames.getSelectionModel().selectFirst();
            });
            lvGames.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Game>) c -> {
                Game game = lvGames.getSelectionModel().getSelectedItem();
                lblGamename.setText(game.getName());
                lblVersion.setText(game.getVersion());
                Call<Boolean> call = service.isUptodate(game.getName());
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.body()){
                            Platform.runLater(() -> lblAvailable.setText("Game is ready to play."));
                            return;
                        }
                        Platform.runLater(() -> lblAvailable.setText("Game has to be Downloaded."));
                    }
                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) { }
                });
            });
            lvGames.setCellFactory(c -> new ListCell<Game>(){
                @Override
                protected void updateItem(Game item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(null);
                    setText(null);
                    if(item != null){
                        if(item.getPosterUrl() != null){
                            ImageView imageView = new ImageView(new Image(item.getPosterUrl(), true));
                            imageView.setFitHeight(138);
                            imageView.setFitWidth(92);
                            setGraphic(imageView);
                        }
                        setText(item.getName());
                    }
                }
            });
        });
    }

    @FXML
    private void download(ActionEvent event){
        Call<Integer> call = service.download(lvGames.getSelectionModel().getSelectedItem().getName());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) { }
        });
    }

    @FXML
    private void startGame(){

    }

    @FXML
    private void openExplorer(){

    }

    @FXML
    private void startServer(){

    }

    @FXML
    private void connectServer(){

    }
    */
}
