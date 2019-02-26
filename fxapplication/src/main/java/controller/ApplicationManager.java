package controller;

import clientInterface.Client;
import entities.Game;
import entities.GameList;
import entities.GameStatusProperty;
import entities.User;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import stages.*;

public class ApplicationManager {
    private static PreloaderStage preloaderStage;
    private static LoginStage loginStage;
    private static MainStage mainStage;
    private static UsersStage usersStage;
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
        client.sendUserData(username, gamepath);
        mainStage = new MainStage();
        usersStage = new UsersStage();
        mainStage.show();
        loginStage.hide();
        loginStage = new LoginStage();
    }

    public static void saveSettings(String username, String gamepath){
        client.sendUserData(username, gamepath);
        loginStage.hide();
    }

    public static void showUsers(){
        usersStage.show();
    }

    public static void showSettings(){
        loginStage.show();
    }

    public static void closeAllMainStages(){
        usersStage.hide();
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

    public static boolean isRunning(){
        return preloaderStage.isShowing() || loginStage.isShowing() || mainStage.isShowing();
    }

    public static boolean isPreloader(){
        return preloaderStage.isShowing();
    }

    public static boolean isMainstage(){
        return mainStage != null;
    }

    public static String getUsername(){
        return client.getUser().getUsername();
    }

    public static String getGamepath(){
        return client.getUser().getGamepath();
    }

    public static GameList getGames(){
        return client.getGames();
    }

    public static void setServerStatusLabel(Label lblStatus){
        client.setServerStatusLabel(lblStatus);
    }

    public static ObservableList<User> getUserslist(){
        return client.getUsersList();
    }

    public static boolean isConnected(){
        return client.getServerStatus().isConnected();
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
        serverconnectstage.hide();
    }

    public static User getUser(){
        return client.getUser();
    }

}
