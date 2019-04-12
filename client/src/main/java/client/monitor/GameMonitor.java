package client.monitor;

import client.LANClient;
import entities.game.Game;

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

    @Override
    public boolean stop(Game game){
        for(GameProcess process : this){
            if(process.getGame().equals(game))
                return process.kill();
        }
        return false;
    }

}
