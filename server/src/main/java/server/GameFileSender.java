package server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class GameFileSender extends Thread{
    private Socket socket;
    private String gameName, username;
    private File gameFile;


    GameFileSender(String hostIPAddress, int port, File gameFile, String gameName, String username) {
        this.gameFile = gameFile;
        this.gameName = gameName;
        this.username = username;
        try {
            socket = new Socket(hostIPAddress, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }

    @Override
    public void run() {
        try {
            sendFile(gameFile);
        } catch (IOException e) {
            System.err.println("ERROR: Sending '" + gameName + "'.\n");
            e.printStackTrace();
        }
    }

    private void sendFile(File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1048576];

        System.out.println("SENDING: '" + gameName + "' to " + username + ".\n");
        int read;
        while ((read=fis.read(buffer)) > 0) {
            dos.write(buffer,0,read);
        }
        fis.close();
        dos.close();
        socket.close();
        System.out.println("SENT: '" + gameName + "' to " + username + ".\n");
    }
}