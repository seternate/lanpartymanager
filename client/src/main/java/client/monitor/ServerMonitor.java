package client.monitor;

import client.LANClient;
import entities.game.Game;

/**
 * Handles all GameProcesses for the servers.
 */
public class ServerMonitor extends Monitor{

    public ServerMonitor(LANClient client){
        this.client = client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(GameProcess gameprocess){
        boolean returnValue = super.add(gameprocess);
        client.updateOpenServers();
        return returnValue;
    }

    @Override
    boolean removeAndUpdate(GameProcess gameprocess) {
        boolean removed;
        synchronized(this){
            removed = remove(gameprocess);
        }
        if(removed)
            client.updateOpenServers();
        return removed;
    }

    @Override
    public boolean stop(Game game) {
        //TODO
        return false;
    }

}
