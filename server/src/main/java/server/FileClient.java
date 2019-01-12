package server;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileClient extends Thread{
    private Socket s;
    private String file, gamename, username;


    FileClient(String host, int port, String file, String gamename, String username) {
        this.file = file;
        this.gamename = gamename;
        this.username = username;
        try {
            s = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }

    @Override
    public void run() {
        try {
            sendFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String file) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1048576];

        System.out.println("Sending " + gamename + " to " + username);

        int read;
        while ((read=fis.read(buffer)) > 0) {
            dos.write(buffer,0,read);
        }
        fis.close();
        dos.close();
        s.close();

        System.out.println("Finished sending " + gamename + " to " + username);
    }
}
