package main;

import entities.Game;
import server.Server;

/**
 * Entrypoint for the lan-server.
 */
public class Main {
    /**
     * {@link Server} object used for the lan-server.
     */
    private static Server server;

    /**
     * <code>Main</code> method.
     * @param args Console Arguments used for this program. (None in use)
     */
    public static void main(String[] args) {
        server = Server.build();
        server.start();

        for(Game game : server.getGamelist()){
            System.out.println("Name: " + game.getName());
            System.out.println("Version: " + game.getVersion());
            System.out.println("Exe-Pfad: " + game.getExeFileRelative());
            System.out.println("Parameter: " + game.getConnectParam());
            System.out.println("Paramter(127.0.0.1): " + game.getConnectParam("127.0.0.1"));
            System.out.println("\n");
        }
    }
}
