package client.download;


import entities.game.Game;
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
 * Creates a new socket on an open port to listen for an incoming gamefile. Downloads the gamefile and extracts the
 * 7zip file after downloading. Also tracks the progress of the download and the extraction.
 */
public class GameDownload extends Thread {
    private static Logger log = Logger.getLogger(GameDownload.class);

    private Game game;
    private ServerSocket serversocket;
    private Socket clientsocket;
    private GameDownloadManager manager;
    private String gamepath;
    private int port;
    private volatile double downloadprogress, unzipprogress;
    private volatile long gamesize, remaining, downloadspeed, averageDownloadspeed;
    private volatile boolean stop;


    /**
     * Creates the socket on a free port to listen for incoming gamefiles.
     *
     * @param game game to be downloaded.
     * @param user user downloading the game.
     */
    public GameDownload(Game game, User user) {
        this.game = game;
        this.gamepath = user.getGamepath();
        clientsocket = new Socket();
        downloadprogress = 0.;
        unzipprogress = 0.;
        gamesize = 0;
        remaining = 0;
        downloadspeed = 0;
        averageDownloadspeed = 0;
        stop = false;
        //Open server socket on any free port
        port = NetworkHelper.getOpenPort();
        try {
            serversocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Can not open the server socket on port: " + port, e);
        }
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * {@link #getFileSizeAndConnect()} MUST be called first or {@link #run()} won't be called.
     */
    @Override
    public void start(){
        if(gamesize == 0) {
            log.error("Client-socket is not connected. Call 'getFileSizeAndConnect()' first.");
            return;
        }
        super.start();
    }

    @Override
    public void run() {
        //Save incoming file and remove this GameDownload after finished
            try {
                saveFile();
            } catch (Exception e) {
                if(stop) {
                    return;
                }
                log.error("Error while saving/extracting the gamefile.", e);
            } finally {
                manager.remove(this);
            }
    }

    /**
     * This method MUST be called before saving the file. This is needed, because the client socket won't be connected
     * to the server.
     *
     * @return the size of the downloaded gamefile [bytes].
     */
    public long getFileSizeAndConnect(){
        while (!clientsocket.isConnected()) {
            try {
                clientsocket = serversocket.accept();
                DataInputStream dis = new DataInputStream(clientsocket.getInputStream());
                gamesize = dis.readLong();
                remaining = gamesize;
            } catch (IOException e) {
                log.error("Error while reading the gamesize.", e);
            }
        }
        return gamesize;
    }

    /**
     * Receives the file from the LANServer. First stores the gamefile size and checks if enough free space is
     * available on the disk specified by the gamepath, then downloads the game itself and updates the progress
     * and speed information of the download. Also handles the extraction of the 7zip gamefile downloaded and deletes
     * the 7zip file if the extraction was successful.
     *
     * @throws IOException if any error while reading or writing occurs an exception is thrown.
     */
    private void saveFile() throws IOException {
        //Get filename
        String gamepath = this.gamepath + game.getServerFileName();
        //Open inputstream
        DataInputStream dis = new DataInputStream(clientsocket.getInputStream());
        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];
        //Open Fileoutputstream
        FileOutputStream fos = new FileOutputStream(gamepath, false);
        //Read game data
        //Check if download got canceled
        if(stop)
            return;
        log.info("Started download of '" + game + "' with a size of " + (double)Math.round((double)gamesize/10485.76)/100. + " MByte.");
        int read = 1;
        long readSum = 0;
        long durationSum = 0;
        do {
            //Start timer to provide speed information
            long start = System.nanoTime();
            //Read data
            read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining));
            //Calculate remaining
            synchronized (this) {
                remaining -= read;
            }
            //Safe data
            fos.write(buffer, 0, read);
            //Calculate duration and set download speed
            long duration = (System.nanoTime() - start == 0) ? 1 : Math.abs(System.nanoTime() - start);
            durationSum += duration;
            readSum += read;
            //Set current speed in bytes/second
            downloadspeed = Math.round((double)read/((double)duration/1000000000.));
            //Set average speed in bytes/second
            averageDownloadspeed = Math.round((double)readSum/((double)durationSum/1000000000.));
            //Set progress
            downloadprogress = (double)readSum/(double)gamesize;
        } while(read > 0 && !stop);
        //Close all streams
        fos.close();
        dis.close();
        clientsocket.close();
        serversocket.close();
        if(stop) {
            log.info("Download of '" + game + "' was stopped.");
            return;
        }
        log.info("Download of '" + game + "' ended successfully - "
                + (double)Math.round((double)getAverageDownloadspeed()/10485.76)/100. + " MByte/sec.");

        //Unzip downloaded game
        log.info("Start unzipping '" + game + "'.");
        try {
            new SevenZipHelper(gamepath, this.gamepath, false, null, this).extract();
            log.info("Unzipping of '" + game + "' finished successfully.");
            //Delete 7zip file from the download
            File file = new File(gamepath);
            if(!file.delete())
                log.error("Can not delete 7zip file of '" + game + "'.");
            else {
                log.info("Deleted 7zip file of '" + game + "'.");
            }
        } catch (SevenZipHelper.ExtractionException e) {
            log.error("Unzipping of '" + game + "' to '" + gamepath + "' failed.", e);
        }

    }

    /**
     * Sets the manager for this gamefile download.
     *
     * @param manager GameDownloadManager that should manage this gamefile download.
     */
    void setManager(GameDownloadManager manager){
        this.manager = manager;
    }

    /**
     * @return the downloaded game
     */
    public Game getGame(){
        return game;
    }

    /**
     * @return port of the server waiting to receive the game data.
     */
    public int getPort(){
        return port;
    }

    /**
     * Stops the download or extraction of the gamefile.
     */
    public void stopDownloadUnzip(){
        stop = true;
    }

    /**
     * @return true if the download and unzip thread should be stopped, else false.
     */
    public boolean isStopped(){
        return stop;
    }

    /**
     * Returns the progress of the download with a precision of 4 as decimal.
     *
     * @return progress of the download.
     */
    public double getDownloadprogress(){
        return (double)Math.round(downloadprogress*10000.)/10000.;
    }

    /**
     * @return remaining size of the gamefile to download [bytes].
     */
    public long getSizeRemaining(){
        return remaining;
    }

    /**
     * Returns the current download speed after each send package. For average speed see {@link #getAverageDownloadspeed()}.
     *
     * @return current download speed [bytes/second].
     */
    public long getDownloadspeed(){
        return downloadspeed;
    }

    /**
     * Returns the average download speed. For current speed see {@link #getDownloadspeed()}.
     *
     * @return average download speed [bytes/second].
     */
    public long getAverageDownloadspeed(){
        return averageDownloadspeed;
    }

    /**
     * Returns the progress of the unzipping of the downloaded gamefile with a precision of 4 as decimal.
     *
     * @return progress of the unzipping.
     */
    public double getUnzipprogress(){
        return (double)Math.round(unzipprogress*10000.)/10000.;
    }

    /**
     * Only for SevenZipHelper to set the unzip progress.
     *
     * @param progress progress of the unzipping.
     */
    void setUnzipprogress(double progress){
        unzipprogress = progress;
    }

}
