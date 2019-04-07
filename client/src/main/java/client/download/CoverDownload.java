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
 * Handles the download for all gamecovers.
 */
public class CoverDownload extends Thread {
    private static Logger log = Logger.getLogger(CoverDownload.class);


    private Socket clientsocket;
    private ServerSocket serversocket;
    private int port;
    private String gamepath;


    /**
     * Creates the CoverDownload object.
     *
     * @param user user that downloads the game.
     */
    public CoverDownload(User user){
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

    @Override
    public void run(){
        while (!clientsocket.isConnected()) {
            try {
                clientsocket = serversocket.accept();
                saveCovers();
            } catch (IOException e) {
                log.error("Error while saving the covers.", e);
            }
        }
    }

    /**
     * Saves all covers to the gamepath of the user.
     *
     * @throws IOException if any IO-Error occurs.
     */
    private void saveCovers() throws IOException {
        //Open inputstream
        DataInputStream dis = new DataInputStream(clientsocket.getInputStream());
        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];

        int files = dis.readInt();

        log.info("Started download of game covers.");
        for(int i = 0; i < files; i++){
            //Get file size
            long filesize = dis.readLong();
            //Get filename
            File coverpath = new File(this.gamepath, "cover");
            if(!coverpath.exists())
                coverpath.mkdirs();
            File coverfile = new File(coverpath, dis.readUTF());
            //Open Fileoutputstream
            FileOutputStream fos = new FileOutputStream(coverfile, false);

            //Read game data
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
        log.info("Downloaded all covers.");
    }

    /**
     * @return port this CoverDownload listens to for incoming covers.
     */
    public int getPort(){
        return port;
    }

}
