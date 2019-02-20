package controller;

import clientInterface.Client;
import entities.Game;
import entities.GameList;
import entities.GameStatus;
import entities.GameStatusProperty;
import javafx.scene.control.Label;
import stages.LoginStage;
import stages.MainStage;
import stages.PreloaderStage;

public class ApplicationManager {
    private static PreloaderStage preloaderStage;
    private static LoginStage loginStage;
    private static MainStage mainStage;
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
        mainStage.show();
        loginStage.hide();
    }

    public static void startGame(Game game){
        client.startGame(game);
    }

    public static void downloadGame(Game game){
        client.downloadGame(game);
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
