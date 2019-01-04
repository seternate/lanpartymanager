package client;

import entities.Game;
import helper.PropertiesHelper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer extends Thread {
/*
    private ServerSocket ss;
    private Game game;
    private long filesize;

    public FileServer(int port, Game game, long filesize) {
        this.game = game;
        this.filesize = filesize;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void saveFile(Socket clientSock) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        String absolutePath = PropertiesHelper.getGamepath() + game.getProperties().getProperty("file");

        int filesize = 1073741824;
        byte[] buffer = new byte[1048576];
        FileOutputStream fos = new FileOutputStream(absolutePath, false);

        for(int i = 0; i <= (this.filesize/filesize); i++){
            int read = 0;
            int totalRead = 0;
            int remaining = filesize;
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                System.out.println(game.getName() + ": read " + totalRead + " bytes.");
                fos.write(buffer, 0, read);
            }
            if(i==0) {
                fos.close();
                fos = new FileOutputStream(absolutePath, true);
            }
        }
        fos.close();
        dis.close();

        try {
            new SevenZipHelper(absolutePath, PropertiesHelper.getGamepath(), false, null).extract();
        } catch (SevenZipHelper.ExtractionException e) {
            e.printStackTrace();
        }

        File file = new File(absolutePath);
        file.delete();
    }
    */
}
