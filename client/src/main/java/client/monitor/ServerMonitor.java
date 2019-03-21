package client.monitor;

import client.LANClient;

public class ServerMonitor extends Monitor{
    private LANClient client;


    public ServerMonitor(LANClient client){
        this.client = client;
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
