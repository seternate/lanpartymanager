package server;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileClient extends Thread{

    private Socket s;
    private String file;

    public FileClient(String host, int port, String file) {
        this.file = file;
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

    public void sendFile(String file) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];

        int read;
        while ((read=fis.read(buffer)) > 0) {
            dos.write(buffer,0,read);
        }
        fis.close();
        dos.close();
    }
}
