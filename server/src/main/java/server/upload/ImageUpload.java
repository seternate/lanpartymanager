package server.upload;

import entities.user.User;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Handles the upload for all game images.
 */
public class ImageUpload extends Thread {
    private static Logger log = Logger.getLogger(ImageUpload.class);

    private Socket socket;
    private String ipaddress;
    private File imagepath;
    private int port;
    private User user;


    /**
     * Creates the ImageUpload object.
     *
     * @param port port that is opened from the user.
     * @param user user that wants to download the images.
     * @param imagepath path of the images.
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
     * Sends all images. First sends the amount of files, then the filesize, filename and the images.
     *
     * @throws IOException if any IO-Error occurs.
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
