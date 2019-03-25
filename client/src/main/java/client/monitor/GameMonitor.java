package client.monitor;

import client.LANClient;

public class GameMonitor extends Monitor {

    public GameMonitor(LANClient client){
        this.client = client;
    }

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
