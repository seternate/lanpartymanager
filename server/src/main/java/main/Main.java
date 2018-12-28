package main;

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
    }
}
