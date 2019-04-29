package client.monitor;

import client.LANClient;
import entities.game.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Monitor} manages {@link GameProcess}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public abstract class Monitor extends ArrayList<GameProcess> {
    LANClient client;


    /**
     * Adds the {@code gameProcess} to the {@code Monitor} and sets the {@code gameProcess} manager. If <b>false</b>
     * is returned, the {@code gameProcess} is no longer running, while adding it.
     *
     * @param gameProcess {@link GameProcess} to be added
     * @return <b>true</b> if the {@code gameProcess} is added to the monitor, else <b>false</b>
     * @since 1.0
     */
    @Override
    public boolean add(GameProcess gameProcess) {
        if(!gameProcess.isOpen())
            return false;
        gameProcess.setManger(this);
        return super.add(gameProcess);
    }

    /**
     * @return list of {@code games} of all running {@link GameProcess}
     * @since 1.0
     */
    public List<Game> getRunningProcesses(){
        List<Game> games = new ArrayList<>();
        for(GameProcess process : this){
            games.add(process.getGame());
        }
        return games;
    }

    /**
     * Removes the {@code gameProcess} and sends the updated list of {@link GameProcess} to the {@code LANServer}.
     *
     * @param gameProcess {@link GameProcess} to remove
     * @return <b>true</b> if {@code gameProcess} was removed
     * @since 1.0
     */
    abstract boolean removeAndUpdate(GameProcess gameProcess);

    /**
     * @param game {@link Game} to  be stopped
     * @return <b>true</b> if the {@code game} was stopped
     * @since 1.0
     */
    public boolean stop(Game game){
        for(GameProcess process : this){
            if(process.getGame().equals(game))
                return process.kill();
        }
        return false;
    }

}
