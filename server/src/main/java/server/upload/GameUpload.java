package server.upload;

import entities.game.Game;
import entities.user.User;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * {@code GameUpload} handles the upload of a {@link Game} to a {@link User}.
 * <p>
 *     The {@code GameUpload} tracks the progress of the upload. The Upload can be stopped by calling
 *     {@link #stopUpload()}.
 * </p>
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class GameUpload extends Thread{
    private static Logger log = Logger.getLogger(GameUpload.class);

    private Socket socket;
    private String ipaddress;
    private File gamepath;
    private Game game;
    private User user;
    private GameUploadManager manager;
    private volatile double progress;
    private volatile long uploadspeed, averageUploadspeed;
    private volatile boolean stop;


    /**
     * Creates the {@code GameUpload} for the {@code user} to download the {@code game}.
     * <p>
     *     Opens a socket on the open {@code port}.
     * </p>
     *
     * @param port port, which is used on the {@code user} machine for the upload
     * @param gamepath packed gamefile of the {@code game}
     * @param game {@link Game}, which gets uploaded
     * @param user {@link User}, which requested to upload the {@code game}
     * @since 1.0
     */
    public GameUpload(int port, File gamepath, Game game, User user) {
        this.ipaddress = user.getIpAddress();
        this.gamepath = gamepath;
        this.game = game;
        this.user = user;
        progress = 0.;
        uploadspeed = 0;
        stop = false;
        //Try to create a socket
        try {
            socket = new Socket(ipaddress, port);
        } catch(IOException e) {
            log.error("Can't create socket with ip-address: " + ipaddress + ":" + port, e);
        }
        //Start Upload thread
        start();
    }

    @Override
    public void run() {
        try {
            sendGame();
        } catch (Exception e) {
            if(stop) {
                log.info("'" + user + "' stopped downloading '" + game + "'.");
                return;
            }
            log.error("Error while sending '" + game + "' to '" + user + "'("
                    + ipaddress + ").", e);
        } finally {
            manager.remove(this);
        }
    }

    /**
     * Sets the {@link GameUploadManager} for this {@code GameUpload}.
     *
     * @param manager {@code GameUploadManager} to manage this {@code GameUpload}
     * @since 1.0
     */
    void setManager(GameUploadManager manager){
        this.manager = manager;
    }

    /**
     * Sends the gamefile to the {@code LANClient}.
     * <p>
     *     First sends the gamefile size, then the gamefile itself. Updates the progress and speed information of the
     *     upload.
     * </p>
     *
     * @throws IOException if any error while reading or writing occurs an exception is thrown.
     * @since 1.0
     */
    private void sendGame() throws IOException {
        //Open outputstream
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];

        //Sending game file
        File gamefile = new File(gamepath, game.getServerFileName());
        //Open file stream
        FileInputStream fis = new FileInputStream(gamefile);
        //Send file size
        dos.writeLong(gamefile.length());
        log.info("Sending '" + game + "' to '" + user + "'.");
        //Sending game
        int read;
        long readSum = 0;
        long durationSum = 0;
        do {
            //Start timer to provide speed information
            long start = System.nanoTime();
            //Read Data
            read = fis.read(buffer);
            //Check if anything has to be send, else break while
            if(!(read > 0))
                break;
            //Write data
            dos.write(buffer,0, read);
            //Calculate duration and set upload speed
            long duration = (System.nanoTime() - start == 0) ? 1 : Math.abs(System.nanoTime() - start);
            durationSum += duration;
            readSum += read;
            //Set current speed in bytes/second
            uploadspeed = Math.round((double)read/((double)duration/1000000000.));
            //Set average speed in bytes/second
            averageUploadspeed = Math.round((double)readSum/((double)durationSum/1000000000.));
            //Set progress
            progress = (double)readSum/(double)gamefile.length();
        } while(read > 0 && !stop);
        //Close all open streams
        fis.close();
        dos.close();
        socket.close();
        if(stop)
            log.info("'" + user + "' stopped downloading '" + game + "'.");
        else log.info("Sent '" + game + "' successfully to '" + user + "' - "
                + (double)Math.round((double)getAverageUploadspeed()/10485.76)/100. + " MByte/sec.");
    }

    /**
     * Returns the progress of the upload as a decimal. Range is from {@code 0} to {@code 1}. The decimal has a
     * precision of 4 decimals.
     *
     * @return progress of the upload
     * @since 1.0
     */
    public double getProgress(){
        return (double)Math.round(progress*10000.)/10000.;
    }

    /**
     * Returns the uploadspeed for each package. For the average uploadspeed see {@link #getAverageUploadspeed()}.
     *
     * @return current uploadspeed speed [bytes/second]
     * @since 1.0
     */
    public long getUploadspeed(){
        return uploadspeed;
    }

    /**
     * Returns the average uploadspeed. For the uploadspeed for each package see {@link #getUploadspeed()}.
     *
     * @return average uploadspeed [bytes/second]
     * @since 1.0
     */
    public long getAverageUploadspeed(){
        return averageUploadspeed;
    }

    /**
     * @return {@code User}, who requested the upload
     * @since 1.0
     */
    public User getUser(){
        return user;
    }

    /**
     * @return {@code Game}, which is uploaded
     * @since 1.0
     */
    public Game getGame(){
        return game;
    }

    /**
     * Stops the upload.
     *
     * @since 1.0
     */
    public void stopUpload(){
        stop = true;
    }

}