package client;


import entities.Game;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class Download extends Thread {
    Game game;
    int totalParts;
    int receivedParts;
    double downloadProgress;
    double unzipProgress;
    private DownloadManager manager = null;
    private int packageSize = 536870912;
    private ServerSocket ss;
    private String gamepath;


    Download(int port, Game game, long gamesize, String gamepath) {
        this.game = game;
        this.gamepath = gamepath;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalParts = (int)(gamesize/packageSize);
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

    void setManager(DownloadManager manager){
        this.manager = manager;
    }

    private void saveFile(Socket clientSock) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        String path = gamepath + game.getServerFileName();

        byte[] buffer = new byte[1048576];
        FileOutputStream fos = new FileOutputStream(path, false);

        System.out.println("Receiving " + game.getName());
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
                fos = new FileOutputStream(path, true);
            }
            receivedParts = i;
            downloadProgress = (double)(receivedParts+1)/(double)(totalParts+1);
            System.out.println("Received " + (receivedParts+1) + "/" + (totalParts+1));
        }
        fos.close();
        dis.close();
        System.out.println("Received " + game.getName());


        System.out.println("Unzipping " + game.getName());
        try {
            new SevenZipHelper(path, gamepath, false, null, this).extract();
        } catch (SevenZipHelper.ExtractionException e) {
            e.printStackTrace();
        }
        System.out.println("Unzipped " + game.getName());

        System.out.println("Deleted .7z file from " + game.getName());
        File file = new File(path);
        file.delete();

        manager.remove(this);
    }

}
