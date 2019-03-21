package client.monitor;

import client.LANClient;

public class GameMonitor extends Monitor {
    private LANClient client;

    public GameMonitor(LANClient client){
        this.client = client;
    }

    @Override
    boolean update(GameProcess gameprocess) {
        boolean removed;
        synchronized(this){
            removed = remove(gameprocess);
        }
        if(removed)
            client.updateOpenGames();
        return removed;
    }
}
