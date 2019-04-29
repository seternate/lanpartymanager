package client.monitor;

import entities.game.Game;
import org.apache.log4j.Logger;

/**
 * Handles a single started process of a game
 */
public class GameProcess extends Thread{
    private static Logger log = Logger.getLogger(GameProcess.class);

    private Game game;
    private Process process;
    private Monitor monitor;


    /**
     * Creates a new GameProcess and start listening on the process to end.
     *
     * @param game the corresponding game of the process.
     * @param process process of the game.
     */
    public GameProcess(Game game, Process process){
        this.game = game;
        this.process = process;
        if(process.isAlive())
            start();
    }

    /**
     * Listen on the process until it ends and removes it from the underlying monitor.
     */
    @Override
    public void run() {
        log.info("'" + game + "' has been started successfully.");
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            log.warn("Listening on the game process of '" + game + "' was interrupted.", e);
        }
        log.info("'" + game + "' has been closed with the exit value: " + process.exitValue());
        monitor.removeAndUpdate(this);
    }

    /**
     * @param monitor GameMonitor for this GameProcess
     */
    void setManger(Monitor monitor){
        this.monitor = monitor;
    }

    /**
     * @return true if the game process is alive, else false.
     */
    boolean isOpen(){
        return process.isAlive();
    }

    /**
     * @return game this gameprocess is attached to.
     */
    public Game getGame(){
        return game;
    }

    public boolean kill(){
        process.destroy();
        if(process.isAlive())
            return false;
        return true;
    }

}
