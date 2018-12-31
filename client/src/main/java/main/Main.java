package main;

import client.Client;

public class Main {

    public static Client client;

    public static void main(String[] args) {
        client = new Client();
        client.start();
        System.out.println("Request downloading game...");
        System.out.println(client.downloadGame(client.getGamelist().get(0)));
        System.out.println(client.downloadGame(client.getGamelist().get(1)));
    }
}
