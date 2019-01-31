package controller;

import clientInterface.Client;
import stages.PreloaderStage;

public class ApplicationManager {
    private static PreloaderStage preloaderStage = null;
    private static Client client = null;

    public static void startApplication(){
        client = new Client();
        preloaderStage = new PreloaderStage();
        preloaderStage.show();
    }

    public static void notifyPreloader(){
        preloaderStage.openMainStage();
        preloaderStage.hide();
    }
}
