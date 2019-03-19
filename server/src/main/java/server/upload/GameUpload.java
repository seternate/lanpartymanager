package server.upload;

import entities.Game;
import entities.User;
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
    private File gamefile;
    private Game game;
    private User user;
    private GameUploadManager manager;
    private volatile double progress;
    private volatile long uploadspeed, averageUploadspeed;


    /**
     * Creates a socket for the gamefile and starts sending the gamefile to the user.
     *
     * @param ipaddress ip-address of the user.
     * @param port port, which is opened on the users machine for this download.
     * @param gamefile 7zip file of the game.
     * @param game game, which gets uploaded.
     * @param user user, which requested to download the game.
     */
    public GameUpload(String ipaddress, int port, File gamefile, Game game, User user) {
        this.ipaddress = ipaddress;
        this.gamefile = gamefile;
        this.game = game;
        this.user = user;
        progress = 0.;
        uploadspeed = 0;
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
            sendFile(gamefile);
        } catch (Exception e) {
            log.error("Error while sending '" + game + "' to '" + user + "'("
                    + ipaddress + ".", e);
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
     * @param gamefile 7zip file of the game.
     * @throws IOException if any error while reading or writing occurs an exception is thrown.
     */
    private void sendFile(File gamefile) throws IOException {
        //TODO: send cover image first
        //TODO: send progress
        //Open outputstream
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        //Open Fileinputstream
        FileInputStream fis = new FileInputStream(gamefile);

        //Send filesize
        dos.writeLong(gamefile.length());

        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];

        log.info("Sending '" + game + "' to '" + user + "'.");
        //Sending game
        int read;
        long readSum = 0;
        double durationSum = 0;
        while((read = fis.read(buffer)) > 0) {
            //Start timer to provide speed information
            long start = System.currentTimeMillis();
            //Write data
            dos.write(buffer,0,read);
            //Calculate duration and set upload speed
            long duration = (System.currentTimeMillis() - start == 0) ? 1 : System.currentTimeMillis() - start;
            durationSum += (double)duration/1000.;
            readSum += read;
            //Set current speed in bytes/millis
            uploadspeed = read/duration;
            //Set average speed in bytes/second
            averageUploadspeed = Math.round((double)readSum/durationSum);
            //Set progress
            progress += (double)read/(double)gamefile.length();

        }
        //Close all open streams
        fis.close();
        dos.close();
        socket.close();
        log.info("Sent '" + game + "' successfully to '" + user + "'.");
    }

    /**
     * Returns the progress of the upload to the user with a precision of 4 as decimal.
     *
     * @return progress of the upload
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
        return uploadspeed * 1000;
    }

    /**
     * Returns the average upload speed. For current speed see {@link #getUploadspeed()}.
     *
     * @return average upload speed [bytes/second].
     */
    public long getAverageUploadspeed(){
        return averageUploadspeed;
    }
}