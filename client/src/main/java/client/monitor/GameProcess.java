package client.monitor;

import entities.game.Game;
import main.LanClient;
import org.apache.log4j.Logger;

/**
 * {@code GameProcess} handles the {@link Process} of a {@link Game} running as {@code game} or {@code server}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class GameProcess extends Thread{
    private static Logger log = Logger.getLogger(GameProcess.class);

    private Game game;
    private Process process;
    private Monitor monitor;


    /**
     * Creates the {@code GameProcess} and calls {@link #start()}.
     *
     * @param game corresponding {@link Game} of the {@code process}
     * @param process {@link Process} of the {@code game}
     * @since 1.0
     */
    public GameProcess(Game game, Process process){
        this.game = game;
        this.process = process;
        if(process.isAlive())
            start();
    }

    /**
     * Listen on the {@link Process} of the {@code game} until it is closed. Then removes it from the underlying
     * {@link Monitor}.
     *
     * @since 1.0
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
     * @param monitor {@link Monitor} managing this {@code GameProcess}
     * @since 1.0
     */
    void setManger(Monitor monitor){
        this.monitor = monitor;
    }

    /**
     * @return <b>true</b> if the {@code GameProcess} is alive, else <b>false</b>
     * @since 1.0
     */
    boolean isOpen(){
        return process.isAlive();
    }

    /**
     * @return {@link Game} that {@code GameProcess} is attached to
     * @since 1.0
     */
    public Game getGame(){
        return game;
    }

    /**
     * Kills the {@code GameProcess}.
     *
     * @return <b>true</b> if the {@code GameProcess} was killed, else <b>false</b>
     * @since 1.0
     */
    public boolean kill(){
        process.destroy();
        if(process.isAlive())
            return false;
        return true;
    }

}
