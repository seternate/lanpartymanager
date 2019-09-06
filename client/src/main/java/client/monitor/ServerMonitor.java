package client.monitor;

import client.LANClient;
import main.LanClient;

/**
 * {@code ServerMonitor} manages {@link GameProcess} if the server is running.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ServerMonitor extends Monitor{

    /**
     * Creates the {@code ServerMonitor}.
     *
     * @param client {@link LANClient} for this {@code ServerMonitor}
     * @since 1.0
     */
    public ServerMonitor(LANClient client){
        this.client = client;
    }

    /**
     * Calls {@link Monitor#add(GameProcess)} and {@link LANClient#updateOpenServers()}.
     *
     * @param gameProcess {@link GameProcess} to be added
     * @return see {@link Monitor#add(GameProcess)}
     * @since 1.0
     */
    @Override
    public boolean add(GameProcess gameProcess){
        boolean returnValue = super.add(gameProcess);
        client.updateOpenServers();
        return returnValue;
    }

    @Override
    boolean removeAndUpdate(GameProcess gameprocess) {
        boolean removed;
        synchronized(this){
            removed = remove(gameprocess);
            LanClient.client.getGameStatus(gameprocess.getGame()).setServerRunning(false);
        }
        if(removed)
            client.updateOpenServers();
        return removed;
    }

}
