package main;

import server.Server;

public class Main {
    private static Server server;

    public static void main(String[] args) {
        server = Server.build();
        server.start();
    }
}
