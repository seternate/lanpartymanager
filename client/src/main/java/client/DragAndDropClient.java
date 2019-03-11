package client;

import entities.User;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class DragAndDropClient extends Thread {
    private User user;
    private List<File> files;

    public DragAndDropClient(User user, List<File> files){
        this.user = user;
        this.files = files;
        start();
    }

    @Override
    public void run() {
        try {
            System.out.println("here");
            Socket socket = new Socket(user.getIpAddress(), 1337);
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            //Write number of files to be send
            dos.writeInt(files.size());

            //Write all filenames and filesizes
            for (int i = 0; i < files.size(); i++){
                int filesize = (int)files.get(i).length();
                dos.writeUTF(files.get(i).getName());
                dos.writeInt(filesize);
            }

            //Writing files
            for (int i = 0; i < files.size(); i++){
                //10MByte
                int maxPackageSize = 10485760;
                byte [] buffer = new byte [maxPackageSize];
                FileInputStream fis = new FileInputStream(files.get(i));

                //Iterate over needed packages
                int read;
                while ((read=fis.read(buffer)) > 0) {
                    dos.write(buffer,0,read);
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
