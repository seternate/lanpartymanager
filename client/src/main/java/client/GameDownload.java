package client;


import entities.game.Game;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//TODO
public final class GameDownload extends Thread {
    Game game;
    int totalParts;
    int receivedParts;
    double downloadProgress;
    double unzipProgress;
    private GameDownloadManager manager = null;
    private int packageSize = 10485760;
    private ServerSocket ss;
    private String gamepath;


    GameDownload(int port, Game game, long gamesize, String gamepath) {
        this.game = game;
        this.gamepath = gamepath;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //totalParts = (int)(gamesize/packageSize);
        receivedParts = 0;
        downloadProgress = 0;
        this.start();
    }

    public void run() {
        Socket clientSock = new Socket();
        while (!clientSock.isConnected()) {
            try {
                clientSock = ss.accept();
                saveFile(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setManager(GameDownloadManager manager){
        this.manager = manager;
    }

    private void saveFile(Socket clientSock) throws IOException {
        long gamesize;

        //Get filename
        String gamepath = this.gamepath + game.getServerFileName();

        //Open inputstream
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        //Open Fileoutputstream
        FileOutputStream fos = new FileOutputStream(gamepath, false);

        //Read filesize
        gamesize = dis.readLong();
        totalParts = (int)(gamesize/packageSize);

        //Create buffer [1MByte]
        byte[] buffer = new byte[1048576];

        System.out.println("DOWNLOAD: Started download of '" + game.getName() + "'.");
        for(int i = 0; i <= totalParts; i++){
            @SuppressWarnings("UnusedAssignment")
            int read = 0;
            int remaining = packageSize;
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                remaining -= read;
                fos.write(buffer, 0, read);
            }
            if(i==0) {
                fos.close();
                fos = new FileOutputStream(gamepath, true);
            }
            receivedParts = i;
            downloadProgress = (double)(receivedParts+1)/(double)(totalParts+1);
        }
        fos.close();
        dis.close();
        System.out.println("DOWNLOAD: Ended download of '" + game.getName() + "'.");


        System.out.println("UNZIP: Started unzipping '" + game.getName() + "'.");
        try {
            new SevenZipHelper(gamepath, this.gamepath, false, null, this).extract();
        } catch (SevenZipHelper.ExtractionException e) {
            e.printStackTrace();
        }
        System.out.println("UNZIP: Finished unzipping '" + game.getName() + "'.");

        System.out.println("UNZIP: Deleted 7-ZIP file of '" + game.getName() + "'.");
        File file = new File(gamepath);
        //noinspection ResultOfMethodCallIgnored
        file.delete();

        manager.remove(this);
    }

}
