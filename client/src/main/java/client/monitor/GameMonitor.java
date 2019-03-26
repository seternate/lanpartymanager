package client.monitor;

import client.LANClient;

/**
 * Handles all GameProcesses for the games.
 */
public class GameMonitor extends Monitor {

    public GameMonitor(LANClient client){
        this.client = client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(GameProcess gameprocess){
        boolean returnValue = super.add(gameprocess);
        client.updateOpenGames();
        return returnValue;
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
