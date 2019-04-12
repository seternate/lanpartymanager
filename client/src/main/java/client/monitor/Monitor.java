package client.monitor;

import client.LANClient;
import entities.game.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for handling any GameProcess.
 */
public abstract class Monitor extends ArrayList<GameProcess> {
    LANClient client;


    /**
     * Adds the gameprocess to the monitor and sets the process manager if the process is running.
     *
     * @param gameProcess gameprocess to add to the monitor.
     * @return true if the gameprocess is added to the monitor, else false.
     */
    @Override
    public boolean add(GameProcess gameProcess) {
        if(!gameProcess.isOpen())
            return false;
        gameProcess.setManger(this);
        return super.add(gameProcess);
    }

    /**
     * @return List of all running gameprocess managed by this monitor.
     */
    public List<Game> getRunningProcesses(){
        List<Game> games = new ArrayList<>();
        for(GameProcess process : this){
            games.add(process.getGame());
        }
        return games;
    }

    /**
     * Removes the gameprocess from this monitor (thread-safe) and sends the new list of gameprocesses to the server.
     *
     * @param gameProcess gameprocess to remove from this monitor.
     * @return true if this monitor contained the gameprocess and removed it.
     */
    abstract boolean update(GameProcess gameProcess);

    public abstract boolean stop(Game game);
}
