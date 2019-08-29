package controller;

import entities.game.Game;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.WindowEvent;
import main.LanClient;
import stages.*;

import java.util.Timer;
import java.util.TimerTask;

public class ApplicationManager {
    private static PreloaderStage preloaderStage;
    private static LoginStage loginStage;
    private static MainStage mainStage;
    private static SettingsStage settingsStage;
    private static UsersStage usersStage;
    private static OrderStage orderStage;
    private static ServerStartStage serverstartstage;


    public static void start(){
        preloaderStage = new PreloaderStage();
        preloaderStage.show();
        preloaderStage.setOnHidden(event -> {
            if(!ApplicationManager.isRunning())
                System.exit(0);
        });
        //LoginStage timer/executor after first server connection was made
        Timer loginOpener = new Timer();
        loginOpener.schedule(new TimerTask() {
            @Override
            public void run() {
                if(LanClient.client.getStatus().isConnected())
                    Platform.runLater(() -> openLoginStage());
                else
                    LanClient.client.getStatus().getConnectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if(newValue) {
                                LanClient.client.getStatus().getConnectedProperty().removeListener(this);
                                Platform.runLater(() -> openLoginStage());
                            }
                        }
                    });
                loginOpener.cancel();
            }
        }, preloaderStage.getAnimationCycleDuration());
    }

    public static void openLoginStage(){
        loginStage = new LoginStage();
        loginStage.show();
        loginStage.setOnHidden(event -> {
            if(!ApplicationManager.isRunning())
                System.exit(0);
        });
        preloaderStage.hide();
    }

    public static void openMainStage(String username, String gamepath){
        LanClient.client.loginServer(username, gamepath);
        mainStage = new MainStage();
        mainStage.setOnHidden(event -> {
            if(!ApplicationManager.isRunning())
                System.exit(0);
        });
        settingsStage = new SettingsStage();
        usersStage = new UsersStage();
        //orderStage = new OrderStage();
        mainStage.show();
        loginStage.hide();
    }

    public static void showUsers(){
        if(usersStage.isShowing())
            usersStage.requestFocus();
        else
            usersStage.show();
    }

    public static void showSettings(){
        if(settingsStage.isShowing())
            settingsStage.requestFocus();
        else
            settingsStage.show();
    }

    public static void showOrder(){
        if(orderStage.isShowing())
            orderStage.requestFocus();
        else
            orderStage.show();
    }

    public static void openServerStartup(Game game){
        serverstartstage = new ServerStartStage(game);
        serverstartstage.show();
    }

    public static void updateMainstageRoot(){
        if(mainStage != null)
            mainStage.updateGames();
    }

    public static void updateMainStageServers(){
        if(mainStage != null)
            mainStage.updateServers();
    }

    public static void updateUsersStage(){
        if(usersStage != null)
            usersStage.update();
    }

    public static boolean isRunning(){
        return preloaderStage.isShowing() || (loginStage != null && loginStage.isShowing()) || (mainStage != null && mainStage.isShowing());
    }

}
