package client.monitor;

import client.LANClient;

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
    boolean update(GameProcess gameprocess) {
        boolean removed;
        synchronized(this){
            removed = remove(gameprocess);
        }
        if(removed)
            client.updateOpenServers();
        return removed;
    }

}
