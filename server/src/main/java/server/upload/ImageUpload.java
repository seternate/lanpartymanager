package server.upload;

import entities.user.User;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * {@code ImageUpload} handles the upload of all {@code Images} served by the {@code LANServer}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ImageUpload extends Thread {
    private static Logger log = Logger.getLogger(ImageUpload.class);

    private Socket socket;
    private String ipaddress;
    private File imagepath;
    private int port;
    private User user;


    /**
     * Creates the {@code ImageUpload}.
     * <p>
     *     A new {@link Socket} on an free port is opened for the upload. Then {@link #start()} is called.
     * </p>
     *
     * @param port port, opened by the {@code user}
     * @param user {@link User}, who requested the upload of the {@code Images}
     * @param imagepath path of the images
     * @since 1.0
     */
    public ImageUpload(int port, User user, File imagepath){
        this.ipaddress = user.getIpAddress();
        this.port = port;
        this.user = user;
        this.imagepath = imagepath;
        //Open socket
        try {
            socket = new Socket(ipaddress, port);
        } catch (IOException e) {
            log.error("Can't create socket with ip-address: " + ipaddress + ":" + port, e);
        }
        //Start the upload
        start();
    }

    /**
     * @since 1.0
     */
    @Override
    public void run(){
        try {
            sendImages();
        } catch (Exception e) {
            log.error("Error while sending the images to '" + user + "' : "
                    + ipaddress + ".", e);
        }
    }

    /**
     * Sends all {@code Images}. First the amount of files, then the filesizes, filename and imagefile are send.
     *
     * @throws IOException if any IO-Error occurs
     * @since 1.0
     */
    private void sendImages() throws IOException {
        //Open outputstream
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];

        //Sending game file
        File[] imagefiles = imagepath.listFiles();
        //Send file amount
        dos.writeInt(imagefiles.length);
        for(File coverfile : imagefiles){
            //Open file stream
            FileInputStream fis = new FileInputStream(coverfile);
            //Send file size
            dos.writeLong(coverfile.length());
            //Send image name
            dos.writeUTF(coverfile.getName());
            //Sending image
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
        log.info("Sent all images successfully to '" + user + "'.");
    }

}
