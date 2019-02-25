package controller;

import clientInterface.Client;
import entities.Game;
import entities.GameList;
import entities.GameStatusProperty;
import entities.User;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import stages.LoginStage;
import stages.MainStage;
import stages.PreloaderStage;
import stages.UsersStage;

public class ApplicationManager {
    private static PreloaderStage preloaderStage;
    private static LoginStage loginStage;
    private static MainStage mainStage;
    private static UsersStage usersStage;
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
    }

    public static void openUsers(){
        usersStage.show();
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
        //Todo
    }

    public static void openServerStartup(Game game){
        //Todo
    }

    public static boolean isRunning(){
        return preloaderStage.isShowing() || loginStage.isShowing() || mainStage.isShowing();
    }

    public static boolean isPreloader(){
        return preloaderStage.isShowing();
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
        mainStage.updateRoot();
    }

}
