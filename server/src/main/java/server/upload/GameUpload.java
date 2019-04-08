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
 * Creates a new socket and transfers a gamefile to a user. Also tracks the send progress.
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
     * Creates a socket for the gamefile and starts sending the gamefile to the user.
     *
     * @param port port, which is opened on the users machine for this download.
     * @param gamepath 7zip file of the game.
     * @param game game, which gets uploaded.
     * @param user user, which requested to download the game.
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
     * Sets the manager for this gamefile transfer.
     *
     * @param manager GameUploadManager that should manage this gamefile transfer.
     */
    void setManager(GameUploadManager manager){
        this.manager = manager;
    }

    /**
     * Sends the file to the user. First writes the gamefile size, then sends the game itself and updates the progress
     * and speed information of the upload.
     *
     * @throws IOException if any error while reading or writing occurs an exception is thrown.
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
     * Returns the progress of the upload to the user with a precision of 4 as decimal.
     *
     * @return progress of the upload.
     */
    public double getProgress(){
        return (double)Math.round(progress*10000.)/10000.;
    }

    /**
     * Returns the current upload speed after each send package. For average speed see {@link #getAverageUploadspeed()}.
     *
     * @return current upload speed [bytes/second].
     */
    public long getUploadspeed(){
        return uploadspeed;
    }

    /**
     * Returns the average upload speed. For current speed see {@link #getUploadspeed()}.
     *
     * @return average upload speed [bytes/second].
     */
    public long getAverageUploadspeed(){
        return averageUploadspeed;
    }

    /**
     * @return user who started the upload.
     */
    public User getUser(){
        return user;
    }

    /**
     * @return game which is uploaded.
     */
    public Game getGame(){
        return game;
    }

    /**
     * Stop the upload.
     */
    public void stopUpload(){
        stop = true;
    }
}