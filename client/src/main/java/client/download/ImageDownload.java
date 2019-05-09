package client.download;

import entities.user.User;
import helper.NetworkHelper;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * {@code ImageDownload} handles the download of all {@code Images} served by the {@code LANServer}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ImageDownload extends Thread {
    private static Logger log = Logger.getLogger(ImageDownload.class);

    private Socket clientsocket;
    private ServerSocket serversocket;
    private int port;
    private String gamepath;


    /**
     * Creates the {@code ImageDownload}.
     * <p>
     *     A new {@link Socket} on an free port is opened for the download. Then {@link #start()} is called.
     * </p>
     *
     * @param user {@link User} who downloads the {@code Images}
     * @since 1.0
     */
    public ImageDownload(User user){
        clientsocket = new Socket();
        gamepath = user.getGamepath();
        //Get a free port to listen to
        port = NetworkHelper.getOpenPort();
        //Open server
        try {
            serversocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Can not open the server socket on port: " + port, e);
        }
        //Start the download
        start();
    }

    /**
     * @since 1.0
     */
    @Override
    public void run(){
        //Tries to connect the socket until a connection is made
        while (!clientsocket.isConnected()) {
            try {
                clientsocket = serversocket.accept();
                saveImages();
            } catch (IOException e) {
                log.error("Error while saving the images.", e);
            }
        }
    }

    /**
     * Downloads and saves all {@code Images} to the {@code gamepath}.
     *
     * @throws IOException if any IO-Error occurs
     * @since 1.0
     */
    private void saveImages() throws IOException {
        //Open inputstream
        DataInputStream dis = new DataInputStream(clientsocket.getInputStream());
        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];

        int files = dis.readInt();

        log.info("Started download of game images.");
        for(int i = 0; i < files; i++){
            //Get file size
            long filesize = dis.readLong();
            //Get filename
            File imagepath = new File(this.gamepath, "images");
            if(!imagepath.exists())
                imagepath.mkdirs();
            File coverfile = new File(imagepath, dis.readUTF());
            //Open Fileoutputstream
            FileOutputStream fos = new FileOutputStream(coverfile, false);

            //Read image data
            int read;
            while((read = dis.read(buffer, 0, (int)Math.min(buffer.length, filesize))) > 0) {
                //Calculate remaining
                filesize -= read;
                //Safe data
                fos.write(buffer, 0, read);
            }
            //Close file streams
            fos.close();
        }
        //Close all streams
        dis.close();
        clientsocket.close();
        serversocket.close();
        log.info("Downloaded all images.");
    }

    /**
     * @return {@code port} of the {@link Socket} for the incoming {@code Images}
     * @since 1.0
     */
    public int getPort(){
        return port;
    }

}
