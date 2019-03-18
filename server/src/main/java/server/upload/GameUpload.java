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
            manager.add(this);
            sendFile(gamefile);
        } catch (IOException e) {
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
     * Sends the file to the user. First writes the gamefile size, then sends the game itself
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

        //Create buffer [100MByte]
        byte[] buffer = new byte[104857600];

        log.info("Sending '" + game + "' to '" + user + "'.");
        //Sending game
        int read;
        while((read = fis.read(buffer)) > 0) {
            //Write data
            dos.write(buffer,0,read);
        }
        //Close all open streams
        fis.close();
        dos.close();
        socket.close();
        log.info("Sent '" + game + "' successfully to '" + user + "'.");
    }
}