package client.download;

import entities.user.User;
import helper.NetworkHelper;
import main.LanClient;
import org.apache.log4j.Logger;
import requests.ImageDownloadRequest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * {@code ImageDownload} handles the download of all {@code Images} served by the {@code LANServer}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ImageDownload extends Thread {
    private static Logger log = Logger.getLogger(ImageDownload.class);
    private static Queue<ImageDownload> queue = new LinkedList<>();

    public static void queue(User user){
        ImageDownload imageDownlaod = new ImageDownload(user);
        queue.add(imageDownlaod);
        if(queue.size() == 1)
            LanClient.client.sendTCP(new ImageDownloadRequest(user.getIpAddress(), imageDownlaod.getPort()));
    }

    public static boolean isDownloading(){
        return queue.size() != 0;
    }

    public static void waitDownloads(){
        try {
            if(queue.size() != 0) {
                synchronized (queue.peek()){
                    queue.peek().wait(2000);
                    waitDownloads();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Socket clientsocket;
    private ServerSocket serversocket;
    private int port;
    private String gamepath, ipAddress;


    private ImageDownload(){ }

    /**
     * Creates the {@code ImageDownload}.
     * <p>
     *     A new {@link Socket} on an free port is opened for the download. Then {@link #start()} is called.
     * </p>
     *
     * @param user {@link User} who downloads the {@code Images}
     * @since 1.0
     */
    private ImageDownload(User user){
        clientsocket = new Socket();
        ipAddress = user.getIpAddress();
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
     * Deletes all previous downloaded {@code Images} first, then downloads and saves all {@code Images} to the {@code gamepath}.
     *
     * @throws IOException if any IO-Error occurs
     * @since 1.0
     */
    private void saveImages() throws IOException {
        //Open inputstream
        DataInputStream dis = new DataInputStream(clientsocket.getInputStream());
        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];
        //Reads number of images to be downloaded
        int files = dis.readInt();
        //Get filename
        File imagepath = new File(this.gamepath, "images");
        if(imagepath.exists()){
            File[] imagelist = imagepath.listFiles(pathname -> pathname.isFile());
            //Delete old images
            for(File image : imagelist){
                if(!image.delete())
                    log.error("Can not delete the image: " + image.getAbsolutePath());
                log.info("Deleted image: " + image.getAbsolutePath());
            }
        } else {
            if(imagepath.mkdirs())
                log.info("Created the image folder: " + imagepath);
        }
        log.info("Started download of game images.");
        for(int i = 0; i < files; i++){
            //Get file size
            long filesize = dis.readLong();
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
        queue.remove();
        if(queue.size() != 0){
            ImageDownload imageDownload = queue.peek();
            LanClient.client.sendTCP(new ImageDownloadRequest(imageDownload.getIpAddress(), imageDownload.getPort()));
        }
    }

    /**
     * @return {@code port} of the {@link Socket} for the incoming {@code Images}
     * @since 1.0
     */
    public int getPort(){
        return port;
    }

    public String getIpAddress(){
        return ipAddress;
    }

}
