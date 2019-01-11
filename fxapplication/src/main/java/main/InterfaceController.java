package main;

import entities.Game;
import entities.GameStatus;
import entities.ServerStatus;
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
import javafx.scene.control.TextField;
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
    ServerStatus status;
    private Retrofit retrofit;
    private FXDataService client;
    private GameStatus gamestatus;


    private ObservableList<Game> games;
    private ObservableList<User> users;

    @FXML
    Label lblStatus;
    @FXML
    private ListView<Game> lvGames;
    @FXML
    private ListView<User> lvUsers;
    @FXML
    private Label lblVersion, lblGamename, lblAvailable;
    @FXML
    private TextField txtServerParam;


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
        lvUsers.setItems(users);
        lvUsers.getSelectionModel().selectFirst();
        lvGames.setItems(games);
        lvGames.getSelectionModel().selectFirst();
        updateGameStatus();
    }

    private void updateGames(){
        Call<List<Game>> callGames = client.getGames(new ArrayList<>(games));
        callGames.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                List<Game> gamesLocal = response.body();
                if(gamesLocal != null) {
                    Platform.runLater(() -> games.setAll(gamesLocal));
                    System.out.println("Updated games.");
                }
                try {
                    sleep(100);
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

    private void updateUsers(){
        Call<List<User>> callUsers = client.getUsers(new ArrayList<>(users));
        callUsers.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.body() != null) {
                    Platform.runLater(() -> users.setAll(response.body()));
                    System.out.println("Updated users.");
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateUsers();
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateUsers();
            }
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
            txtServerParam.setText(item.getServerParam());
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

        users.addListener((ListChangeListener<User>) c -> {
            System.out.println("changed");
            lvUsers.setItems(users);
        });

        lvUsers.setCellFactory(c -> new ListCell<User>(){
            @Override
            protected void updateItem(User item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                if(item != null){
                    setText(item.getName());
                }
            }
        });
    }

    private void updateGameStatus(){
        Game game = lvGames.getSelectionModel().getSelectedItem();
        Call<GameStatus> callGameStatus = client.getGameStatus(game);
        callGameStatus.enqueue(new Callback<GameStatus>() {
            @Override
            public void onResponse(Call<GameStatus> call, Response<GameStatus> response) {
                GameStatus status = response.body();
                gamestatus = status;
                if(status.unzipping)
                    Platform.runLater(() -> lblAvailable.setText("Unzipping: " + ((double)Math.round(status.unzipProgress*1000))/10. + "%"));
                else if(status.downloading)
                    Platform.runLater(() -> lblAvailable.setText("Downloading: " + ((double)Math.round(status.downloadProgress*1000))/10. + "%"));
                else if(status.playable && status.version)
                    Platform.runLater(() -> lblAvailable.setText("Game is playable."));
                else if(status.playable)
                    Platform.runLater(() -> lblAvailable.setText("Game is playable. No version information."));
                else if(status.download)
                    Platform.runLater(() -> lblAvailable.setText(("Download game.")));
                else if(status.update)
                    Platform.runLater(() -> lblAvailable.setText("Update game."));
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateGameStatus();
            }
            @Override
            public void onFailure(Call<GameStatus> call, Throwable t) { }
        });
    }

    @FXML
    private void download(){
        if(gamestatus.downloading || gamestatus.unzipping)
            return;
        Call<Integer> callDownload = client.download(lvGames.getSelectionModel().getSelectedItem());
        callDownload.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body() == -2)
                    Platform.runLater(() -> lblAvailable.setText("Not enough space available."));
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) { }
        });
    }

    @FXML
    private void startGame(){
        Call<Boolean> callStartgame = client.startGame(lvGames.getSelectionModel().getSelectedItem());
        callStartgame.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) { }
        });
    }

    @FXML
    private void openExplorer(){
        Call<Boolean> callOpenexplorer = client.openExplorer(lvGames.getSelectionModel().getSelectedItem());
        callOpenexplorer.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) { }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) { }
        });
    }

    @FXML
    private void startServer(){
        Call<Boolean> callServer = client.startServer(lvGames.getSelectionModel().getSelectedItem(), txtServerParam.getText());
        callServer.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) { }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) { }
        });
    }

    @FXML
    private void connectServer(){
        Call<Boolean> callConnect = client.connect(lvGames.getSelectionModel().getSelectedItem(), lvUsers.getSelectionModel().getSelectedItem().getIp());
        callConnect.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) { }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) { }
        });
    }

}
