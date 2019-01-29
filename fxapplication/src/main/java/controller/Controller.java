package controller;

import clientInterface.Client;

class Controller {
    ApplicationManager manager;
    Client client;

    Controller(){
        manager = ApplicationManager.manager;
        client = manager.getClient();
    }
}
