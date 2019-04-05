package clientInterface;

import controller.ApplicationManager;
import entities.game.Game;
import entities.game.GameList;
import entities.game.GameStatus;
import entities.server.ServerStatus;
import entities.settings.ClientSettings;
import entities.user.User;
import entities.user.UserList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.apache.log4j.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {
    private static Logger log = Logger.getLogger(Client.class);


    private FXDataClient client;
    private volatile ServerStatus status;
    private volatile boolean fileStatus;
    private volatile User user;
    private volatile GameList games;
    private volatile GameStatusProperty gamestatus;
    private volatile Label lblStatus;
    private volatile Label lblFileStatus;
    private volatile ObservableList<User> users;
    private volatile ObservableList<User> orders;
    private volatile UserList userlist;
    private volatile UserList orderlist;
    private ScheduledExecutorService executor;


    public Client(){
        //Client initiation
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/fx/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        client = retrofit.create(FXDataClient.class);
        status = null;
        lblStatus = null;
        lblFileStatus = null;
        games = new GameList();
        gamestatus = new GameStatusProperty();
        users = FXCollections.observableArrayList();
        orders = FXCollections.observableArrayList();
        userlist = new UserList();
        orderlist = new UserList();
        user = new User();
        //ExecutorService for the client updating
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        if(ApplicationManager.isRunning()){
            updateStatusLabel();
            try {
                update();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Client-application connection problems.");
                status = null;
            }
        } else {
            executor.shutdown();
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

    public void setFileStatusLabel(Label lblFileStatus){
        this.lblFileStatus = lblFileStatus;
    }

    public ObservableList<User> getUsersList(){
        return this.users;
    }

    public ObservableList<User> getOrderList(){
        return this.orders;
    }

    public void sendUserData(String username, String gamepath, String order){
        new Thread(() -> {
            User userdata;
            try {
                userdata = new User(new ClientSettings());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            userdata.setUsername(username);
            userdata.setGamepath(gamepath);
            userdata.setOrder(order);
            try {
                client.sendUser(userdata).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public GameStatusProperty getGamestatusProperty(){
        return gamestatus;
    }

    public void startGame(Game game){
        new Thread(() -> {
            try {
                client.startGame(game).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void downloadGame(Game game){
        new Thread(() -> {
            try {
                client.downloadGame(game).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void openExplorer(Game game){
        new Thread(() -> {
            try {
                client.openExplorer(game).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startServer(Game game, String parameters){
        new Thread(() -> {
            try {
                client.startServer(game, parameters).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void connectServer(Game game, String ip){
        new Thread(() -> {
            try {
                client.connectServer(game, ip).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void update() throws IOException {
        user = client.getUser().execute().body();
        if(ApplicationManager.getFocusedGame() != null){
            GameStatus newStatus = client.getGameStatus(ApplicationManager.getFocusedGame()).execute().body();
            assert newStatus != null;
            gamestatus.downloading.setValue(newStatus.isDownloading());
            gamestatus.unzipping.setValue(newStatus.isUnzipping());
            gamestatus.local.setValue(newStatus.isLocal());
            gamestatus.update.setValue(newStatus.isUpdate());
            gamestatus.version.setValue(newStatus.isVersion());
            gamestatus.playable.setValue(newStatus.isPlayable());
            gamestatus.downloadProgress.setValue(newStatus.getDownloadProgress());
            gamestatus.unzipProgress.setValue(newStatus.getUnzipProgress());
            gamestatus.downloadSpeed.setValue(newStatus.getDownloadSpeed());
        }
        updateUsers();
        updateGames();
        status = client.getStatus().execute().body();
        fileStatus = client.getFileStatus().execute().body();
    }

    private void updateGames() throws IOException {
        GameList gamelist = client.getGames().execute().body();
        assert gamelist != null;
        if(!gamelist.equals(games)){
            games = gamelist;
            Platform.runLater(ApplicationManager::updateMainstageRoot);
        }
        games = gamelist;
    }

    private void updateUsers() throws IOException {
        UserList list = client.getUserlist().execute().body();
        assert list != null;
        if(!list.equals(orderlist)){
            UserList temp = new UserList(list);
            Platform.runLater(() -> orders.setAll(temp.toList()));
        }
        orderlist = new UserList(list);

        list.remove(user);
        if(!list.equals(userlist)){
            UserList temp = new UserList(list);
            Platform.runLater(() -> users.setAll(temp.toList()));
        }
        userlist = new UserList(list);
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

        if(lblFileStatus == null)
            return;
        Platform.runLater(() -> {
            if(fileStatus)
                lblFileStatus.setText("(Receiving files ...)");
            else
                lblFileStatus.setText("");
        });
    }

    public void sendFiles(User user, List<File> files){
        new Thread(() -> {
            try {
                client.sendFiles(user, files).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopDownloadUnzip(Game game){
        new Thread(() -> {
            try {
                client.stopDownloadUnzip(game).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
