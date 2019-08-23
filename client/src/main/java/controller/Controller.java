package controller;

import client.LANClient;
import main.LanClient;

public abstract class Controller {
    private LANClient client;


    public Controller(){
        client = LanClient.client;
    }

    public LANClient getClient(){
        return client;
    }

    public abstract void shutdown();
}
