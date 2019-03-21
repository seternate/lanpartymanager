package client.FileDrop;

import entities.user.User;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class FileDropClient extends Thread {
    private User user;
    private List<File> files;

    public FileDropClient(User user, List<File> files){
        this.user = user;
        this.files = files;
        start();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(user.getIpAddress(), 1337);
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            //Write number of files to be send
            dos.writeInt(files.size());

            //Write all filenames and filesizes
            for (File file : files) {
                int filesize = (int) file.length();
                dos.writeUTF(file.getName());
                dos.writeInt(filesize);
            }

            //Writing files
            for (File file : files) {
                //10MByte
                int maxPackageSize = 10485760;
                byte[] buffer = new byte[maxPackageSize];
                FileInputStream fis = new FileInputStream(file);

                //Iterate over needed packages
                int read;
                while ((read = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, read);
                }

                fis.close();
            }

            dos.close();
            os.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
