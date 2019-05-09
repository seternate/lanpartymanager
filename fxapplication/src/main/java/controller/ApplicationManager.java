package controller;

import clientInterface.Client;
import entities.game.Game;
import entities.game.GameList;
import clientInterface.GameStatusProperty;
import entities.server.ServerStatus;
import entities.user.User;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import stages.*;

import java.io.File;
import java.util.List;

public class ApplicationManager {
    private static PreloaderStage preloaderStage;
    private static LoginStage loginStage;
    private static MainStage mainStage;
    private static UsersStage usersStage;
    private static OrderStage orderStage;
    private static ServerbrowserStage serverbrowserStage;
    private static ServerStartStage serverstartstage;
    private static ServerConnectStage serverconnectstage;
    private static Client client;


    public static void start(){
        preloaderStage = new PreloaderStage();
        preloaderStage.show();
        client = new Client();
    }

    public static void openLoginStage(){
        loginStage = new LoginStage();
        loginStage.show();
        preloaderStage.hide();
    }

    public static void openMainStage(String username, String gamepath){
        client.sendUserData(username, gamepath, getUser().getOrder());
        mainStage = new MainStage();
        usersStage = new UsersStage();
        orderStage = new OrderStage();
        serverbrowserStage = new ServerbrowserStage();
        mainStage.show();
        loginStage.hide();
        loginStage = new LoginStage();
    }

    public static void showUsers(){
        if(usersStage.isShowing())
            usersStage.requestFocus();
        else
            usersStage.show();
    }

    public static void showSettings(){
        if(loginStage.isShowing())
            loginStage.requestFocus();
        else
            loginStage.show();
    }

    public static void showOrder(){
        if(orderStage.isShowing())
            orderStage.requestFocus();
        else
            orderStage.show();
    }

    public static void showServerBrowser(){
        if(serverbrowserStage.isShowing())
            serverbrowserStage.requestFocus();
        else
            serverbrowserStage.show();
    }

    public static void closeAllMainStages(){
        usersStage.hide();
        loginStage.hide();
        orderStage.hide();
        serverbrowserStage.hide();
        System.exit(0);
    }

    public static boolean isRunning(){
        return preloaderStage.isShowing() || loginStage.isShowing() || mainStage.isShowing();
    }

    public static boolean isPreloader(){
        return preloaderStage.isShowing();
    }

    public static boolean isMainstage(){
        return mainStage != null;
    }

    public static ServerStatus getServerStatus(){
        return client.getServerStatus();
    }

    public static boolean isConnected(){
        return client.getServerStatus().isConnected();
    }

    public static String getUsername(){
        return client.getUser().getUsername();
    }

    public static String getGamepath(){
        return client.getUser().getGamepath();
    }

    public static void setServerStatusLabel(Label lblStatus){
        client.setServerStatusLabel(lblStatus);
    }

    public static void saveSettings(String username, String gamepath){
        client.sendUserData(username, gamepath, getUser().getOrder());
        loginStage.hide();
    }

    public static void setOrder(String order){
        client.sendUserData(getUsername(), getGamepath(), order);
    }

    public static void stopDownloadUnzip(Game game){
        client.stopDownloadUnzip(game);
    }

    public static void startGame(Game game){
        client.startGame(game);
    }

    public static void downloadGame(Game game){
        client.downloadGame(game);
    }

    public static void openExplorer(Game game){
        client.openExplorer(game);
    }

    public static void openServerList(Game game){
        serverconnectstage = new ServerConnectStage(game);
        serverconnectstage.show();
    }

    public static void openServerStartup(Game game){
        serverstartstage = new ServerStartStage(game);
        serverstartstage.show();
    }

    public static GameList getGames(){
        return client.getGames();
    }

    public static ObservableList<User> getUserslist(){
        return client.getUsersList();
    }

    public static Game getFocusedGame(){
        return mainStage == null ? null : mainStage.getFocusedGame();
    }

    public static GameStatusProperty getGamestatusProperty(){
        return client.getGamestatusProperty();
    }

    public static void updateMainstageRoot(){
        if(mainStage == null)
            return;
        mainStage.updateRoot();
    }

    public static void startServer(Game game, String parameters){
        client.startServer(game, parameters);
        serverstartstage.hide();
    }

    public static void connectServer(Game game, String ip){
        client.connectServer(game, ip);
        if(serverconnectstage != null && serverconnectstage.isShowing())
            serverconnectstage.hide();
    }

    public static ObservableMap<User, Game> getUserRunGames(){
        return client.getRunGamesList();
    }

    public static ObservableMap<User, ObservableList<Game>> getUserRunServers(){
        return client.getRunServerList();
    }

    public static void stopGame(Game game){
        client.stopGame(game);
    }

    public static User getUser(){
        return client.getUser();
    }

    public static ObservableList<User> getOrderList(){
        return client.getUsersList();
    }

    public static void sendFiles(User user, List<File> files){
        client.sendFiles(user, files);
    }

    public static void setFileStatusLabel(Label lblFileStatus){
        client.setFileStatusLabel(lblFileStatus);
    }

}
