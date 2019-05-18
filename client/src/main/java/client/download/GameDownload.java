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
 * {@code GameDownload} handles the download/extraction of a {@link Game}.
 * <p>
 *     Listens for the incoming {@code game} and saves to the {@code gamepath} of the {@code user}. After the download
 *     finished the {@code game} gets extracted and the downloaded file is deleted. The {@code GameDownload} tracks the
 *     progress of the download and extraction. The download or extraction can be stopped by calling
 *     {@link #stopDownloadUnzip()}.
 * </p>
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
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
     * Creates the {@code GameDownload} for the {@code game}.
     * <p>
     *     Opens a socket on any open port.
     * </p>
     *
     * @param game {@link Game} to download
     * @param user {@link User} downloading the {@code game}
     * @since 1.0
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
     * {@link #getFileSizeAndConnect()} <b>MUST</b> be called first or the {@code Thread} won't start.
     * @see Thread#start()
     * @since 1.0
     */
    @Override
    public void start(){
        if(gamesize == 0) {
            log.error("Client-socket is not connected. Call 'getFileSizeAndConnect()' first.");
            return;
        }
        super.start();
    }

    /**
     * @since 1.0
     */
    @Override
    public void run() {
        //Save incoming file and remove this GameDownload after finished
            try {
                saveFile();
            } catch (Exception e) {
                if(stop) {
                    manager.remove(this);
                    return;
                }
                log.error("Error while saving/extracting the gamefile.", e);
            } finally {
                manager.remove(this);
            }
    }

    /**
     * This method <b>MUST</b> be called before calling {@link #start()}. This is needed to connect to the
     * {@code LANServer} socket and get the filesize of the {@code game}.
     *
     * @return size of the gamefile [bytes]
     * @since 1.0
     */
    public long getFileSizeAndConnect(){
        //Try to connect to the serversocket while not connected
        while (!clientsocket.isConnected()) {
            try {
                clientsocket = serversocket.accept();
                DataInputStream dis = new DataInputStream(clientsocket.getInputStream());
                //Read the size of the gamesize
                gamesize = dis.readLong();
                remaining = gamesize;
            } catch (IOException e) {
                log.error("Error while reading the gamesize.", e);
            }
        }
        return gamesize;
    }

    /**
     * Receives the gamefile from the {@code LANServer}.
     * <p>
     *     Downloads the gamefile and updates the progress and speed information of the download. Then handles the
     *     extraction of the 7zip gamefile and deletes the 7zip file if the extraction was successful.
     *     The download or extraction can be interrupted by calling {@link #stopDownloadUnzip()}.
     * </p>
     *
     * @throws IOException if any error while reading or writing occurs an exception is thrown.
     * @since 1.0
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
        int read;
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
     * Sets the {@link GameDownloadManager} for this {@code GameDownload}.
     *
     * @param manager {@code GameDownloadManager} to manage this {@code GameDownload}
     * @since 1.0
     */
    void setManager(GameDownloadManager manager){
        this.manager = manager;
    }

    /**
     * @return {@link Game} of this {@code GameDownload}
     * @since 1.0
     */
    public Game getGame(){
        return game;
    }

    /**
     * @return port of the {@code Socket} opened for the gamefile
     * @since 1.0
     */
    public int getPort(){
        return port;
    }

    /**
     * Stops the download or extraction of the {@code GameDownload}
     * @since 1.0
     */
    public void stopDownloadUnzip(){
        stop = true;
    }

    /**
     * @return <b>true</b> if {@link #stopDownloadUnzip()} was called, else <b>false</b>
     * @since 1.0
     */
    public boolean isStopped(){
        return stop;
    }

    /**
     * Returns the progress of the download as a decimal. Range is from {@code 0} to {@code 1}. The decimal has a
     * precision of 4 decimals.
     *
     * @return progress of the download
     * @since 1.0
     */
    public double getDownloadprogress(){
        return (double)Math.round(downloadprogress*10000.)/10000.;
    }

    /**
     * @return remaining size of the download [bytes]
     * @since 1.0
     */
    public long getSizeRemaining(){
        return remaining;
    }

    /**
     * Returns the downloadspeed for each package. For the average downloadspeed see {@link #getAverageDownloadspeed()}.
     *
     * @return current download speed [bytes/second]
     * @since 1.0
     */
    public long getDownloadspeed(){
        return downloadspeed;
    }

    /**
     * Returns the average downloadspeed. For the downloadspeed for each package see {@link #getDownloadspeed()}.
     *
     * @return average downloadspeed [bytes/second]
     * @since 1.0
     */
    public long getAverageDownloadspeed(){
        return averageDownloadspeed;
    }

    /**
     * Returns the progress of the extraction as a decimal. Range is from {@code 0} to {@code 1}. The decimal has a
     * precision of 4 decimals.
     *
     * @return progress of the extraction
     * @since 1.0
     */
    public double getUnzipprogress(){
        return (double)Math.round(unzipprogress*10000.)/10000.;
    }

    /**
     * <b>ONLY</b> for {@link SevenZipHelper} to set the extraction progress.
     *
     * @param progress progress of the extraction
     * @since 1.0
     */
    void setUnzipprogress(double progress){
        unzipprogress = progress;
    }

}
