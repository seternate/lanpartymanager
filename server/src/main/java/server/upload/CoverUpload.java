package server.upload;

import entities.user.User;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Handles the upload for all gamecovers.
 */
public class CoverUpload extends Thread {
    private static Logger log = Logger.getLogger(CoverUpload.class);

    private Socket socket;
    private String ipaddress;
    private File coverpath;
    private int port;
    private User user;


    /**
     * Creates the CoverUpload object.
     *
     * @param port port that is opened from the user.
     * @param user user that wants to download the covers.
     * @param coverpath path of the covers.
     */
    public CoverUpload(int port, User user, File coverpath){
        this.ipaddress = user.getIpAddress();
        this.port = port;
        this.user = user;
        this.coverpath = coverpath;
        //Open socket
        try {
            socket = new Socket(ipaddress, port);
        } catch (IOException e) {
            log.error("Can't create socket with ip-address: " + ipaddress + ":" + port, e);
        }
        //Start the upload
        start();
    }

    @Override
    public void run(){
        try {
            sendCovers();
        } catch (Exception e) {
            log.error("Error while sending the covers to '" + user + "' : "
                    + ipaddress + ".", e);
        }
    }

    /**
     * Sends all covers. First sends the amount of covers, then the filesize, filename and the cover.
     *
     * @throws IOException if any IO-Error occurs.
     */
    private void sendCovers() throws IOException {
        //Open outputstream
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];

        //Sending game file
        File[] coverfiles = coverpath.listFiles();
        //Send cover amount
        dos.writeInt(coverfiles.length);
        for(File coverfile : coverfiles){
            //Open file stream
            FileInputStream fis = new FileInputStream(coverfile);
            //Send file size
            dos.writeLong(coverfile.length());
            //Send cover name
            dos.writeUTF(coverfile.getName());
            log.info("Sending cover '" + coverfile.getName() + "' to '" + user + "'.");
            //Sending game
            int read;
            while((read = fis.read(buffer)) > 0) {
                //Write data
                dos.write(buffer,0, read);
            }
            //Close file stream
            fis.close();
        }
        //Close all open streams
        dos.close();
        socket.close();
        log.info("Sent all covers successfully to '" + user + "'.");
    }

}
