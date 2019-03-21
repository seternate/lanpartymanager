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
    private GameMonitor monitor;


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
        log.info(game + " has been started successfully.");
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            log.warn("Listening on the game process of '" + game + "' was interrupted.", e);
        }
        log.info(game + " has been closed with the exit value: " + process.exitValue());
        monitor.remove(this);
    }

    /**
     * @param monitor Monitor for this GameProcess
     */
    void setManger(GameMonitor monitor){
        this.monitor = monitor;
    }

    public boolean isOpen(){
        return process.isAlive();
    }

}
