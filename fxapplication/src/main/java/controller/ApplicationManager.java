package controller;

import clientInterface.Client;
import stages.LoginStage;
import stages.PreloaderStage;

public class ApplicationManager {
    private static PreloaderStage preloaderStage = null;
    private static LoginStage loginStage = null;
    private static Client client = null;


    public static void start(){
        preloaderStage = new PreloaderStage();
        preloaderStage.show();
        client = new Client();
    }

    public static void openLoginStage(){
        loginStage = new LoginStage();
        loginStage.show(preloaderStage);
        preloaderStage = null;
    }

    public static boolean isRunning(){
        return preloaderStage == null && loginStage == null;
    }
}
