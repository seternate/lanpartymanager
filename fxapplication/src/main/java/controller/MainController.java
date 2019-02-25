package controller;

import entities.Game;
import entities.GameList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {
    public volatile Game focusedGame;

    @FXML
    private ScrollPane spMain;
    @FXML
    private Label lblStatus;
    @FXML
    private ImageView ivUsers, ivSettings;

    @FXML
    private void initialize(){
        Tooltip.install(ivUsers, new Tooltip("Connected users"));
        Tooltip.install(ivSettings, new Tooltip("Open settings"));
        ApplicationManager.setServerStatusLabel(lblStatus);
        spMain.setFitToWidth(true);
        updateGamePane();
        ivUsers.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ApplicationManager.showUsers();
        });
        ivSettings.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ApplicationManager.showSettings();
        });
    }

    public void updateGamePane(){
        GameList games = ApplicationManager.getGames();
        GridPane tilePane = new GridPane();
        tilePane.setHgap(20);
        tilePane.setVgap(30);
        for(int i = 0; i < games.size(); i++){
            Node gameTile = gameTile(games.get(i));
            tilePane.addRow(i/3, gameTile);
            tilePane.setHgrow(gameTile, Priority.ALWAYS);
            tilePane.setHalignment(gameTile, HPos.CENTER);
        }
        spMain.setContent(tilePane);
    }

    private Node gameTile(Game game){
        ImageView gameTileImage = new ImageView(new Image(game.getCoverUrl(), true));
        gameTileImage.setPreserveRatio(false);
        gameTileImage.fitWidthProperty().bind(spMain.widthProperty().divide(3.5));
        gameTileImage.fitHeightProperty().bind(spMain.widthProperty().multiply(0.4));

        VBox gameTileOverlay = gameTileOverlay(gameTileImage, game);
        gameTileOverlay.prefHeightProperty().bind(gameTileImage.fitHeightProperty());
        gameTileOverlay.prefWidthProperty().bind(gameTileImage.fitWidthProperty());
        gameTileOverlay.setVisible(false);

        StackPane gameTile = new StackPane(gameTileImage, gameTileOverlay);
        gameTile.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            focusedGame = game;
            gameTile.getChildren().get(1).setVisible(true);
            event.consume();
        });
        gameTile.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            gameTile.getChildren().get(1).setVisible(false);
            event.consume();
        });
        return gameTile;
    }

    private VBox gameTileOverlay(ImageView gameTileImage, Game game){
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("gameoverlay.fxml"));
        loader.setController(new GameOverlayController(gameTileImage, game));
        try {
            VBox rootNode = loader.load();
            return rootNode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    ServerStatus status;
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


    MainController(){
        Retrofit retrofit = new Retrofit.Builder()
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

        users.addListener((ListChangeListener<User>) c -> lvUsers.setItems(users));

        lvUsers.setCellFactory(c -> new ListCell<User>(){
            @Override
            protected void updateItem(User item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                if(item != null){
                    setText(item.getUsername());
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
                if(Objects.requireNonNull(status).unzipping)
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
                //noinspection ConstantConditions
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
        Call<Boolean> callConnect = client.connect(lvGames.getSelectionModel().getSelectedItem(), lvUsers.getSelectionModel().getSelectedItem().getIpAddress());
        callConnect.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) { }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) { }
        });
    }
    */

}
