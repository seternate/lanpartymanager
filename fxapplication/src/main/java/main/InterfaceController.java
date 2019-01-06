package main;

import entities.Game;
import entities.Status;
import entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

class InterfaceController{
    Status status;
    private Retrofit retrofit;
    private FXDataService client;


    private ObservableList<Game> games;
    private ObservableList<User> users;

    @FXML
    Label lblStatus;
    @FXML
    private ListView<Game> lvGames;
    @FXML
    private Label lblVersion, lblGamename, lblAvailable;


    InterfaceController(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/fx/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        client = retrofit.create(FXDataService.class);
        games = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();
        updateGames();
        updateUsers();
    }

    void load(){
        initializeUI();
        lvGames.setItems(games);
        lvGames.getSelectionModel().selectFirst();
    }

    private void updateGames(){
        Call<List<Game>> callGames = client.getGames(new ArrayList<>(games));
        callGames.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if(response.body() != null) {
                    Platform.runLater(() -> games.setAll(response.body()));
                    System.out.println("Updated gamelist.");
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateGames();
            }
            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateGames();
            }
        });
    }

    //Todo: Just get new list if something changed, see updateGames()
    private void updateUsers(){
        Call<List<User>> callUsers = client.getUsers();
        callUsers.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Platform.runLater(() -> users.setAll(response.body()));
                System.out.println("Updated userlist.");
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) { }
        });
    }

    private void initializeUI(){
        games.addListener((ListChangeListener<Game>) c -> {
            lvGames.setItems(games);
            lvGames.getSelectionModel().selectFirst();
        });

        lvGames.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Game>) c -> {
            Game item = lvGames.getSelectionModel().getSelectedItem();
            if(item == null)
                return;
            lblGamename.setText(item.getName());
            lblVersion.setText(item.getVersionServer());
            isUptodate(item);
        });

        lvGames.setCellFactory(c -> new ListCell<Game>(){
            @Override
            protected void updateItem(Game item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                if(item != null){
                    if(item.getCoverUrl() != null){
                        ImageView imageView = new ImageView(new Image(item.getCoverUrl(), true));
                        imageView.setFitHeight(138);
                        imageView.setFitWidth(92);
                        setGraphic(imageView);
                    }
                    setText(item.getName());
                }
            }
        });
    }

    private void isUptodate(Game game){
        Call<Integer> callUptodate = client.isGameUptodate(game);
        callUptodate.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer status = response.body();
                if(status == 0)
                    Platform.runLater(() -> lblAvailable.setText("Game is playable."));
                else if(status == -1)
                    Platform.runLater(() -> lblAvailable.setText("Download game."));
                else if(status == -2)
                    Platform.runLater(() -> lblAvailable.setText("Game is playable. No version information."));
                else if(status == -3)
                    Platform.runLater(() -> lblAvailable.setText("Update game."));
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) { }
        });
    }


    @FXML
    private void download(ActionEvent event){
        Call<Integer> callDownload = client.download(lvGames.getSelectionModel().getSelectedItem());
        callDownload.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body() == -2)
                    Platform.runLater(() -> lblAvailable.setText("Not enough space available."));
                else if(response.body() == 0) {
                    Platform.runLater(() -> lblAvailable.setText("Downloading ..."));
                    //Todo: Progress from downloading
                }
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
