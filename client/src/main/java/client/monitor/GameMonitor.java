package client.monitor;

import client.LANClient;
import main.LanClient;

/**
 * {@code GameMonitor} manages {@link GameProcess} if just the game and no server is running.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class GameMonitor extends Monitor {

    /**
     * Creates the {@code GameMonitor}.
     *
     * @param client {@link LANClient} for this {@code GameMonitor}
     * @since 1.0
     */
    public GameMonitor(LANClient client){
        this.client = client;
    }

    /**
     * Calls {@link Monitor#add(GameProcess)} and {@link LANClient#updateOpenGames()}.
     *
     * @param gameProcess {@link GameProcess} to be added
     * @return see {@link Monitor#add(GameProcess)}
     * @since 1.0
     */
    @Override
    public boolean add(GameProcess gameProcess){
        boolean returnValue = super.add(gameProcess);
        client.updateOpenGames();
        return returnValue;
    }

    @Override
    boolean removeAndUpdate(GameProcess gameprocess) {
        boolean removed;
        synchronized(this){
            removed = remove(gameprocess);
            LanClient.client.getGameStatus(gameprocess.getGame()).setRunning(false);
        }
        if(removed)
            client.updateOpenGames();
        return removed;
    }

}
