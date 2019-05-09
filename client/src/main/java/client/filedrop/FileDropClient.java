package client.filedrop;

import entities.user.User;
import helper.NetworkHelper;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * {@code FileDropClient} handles the upload of any files to another {@code LANClient} logged in the {@code LANServer}.
 * <p>
 *     Only single/multiple files can be send. Folders with files are <b>not</b> supported.
 * </p>
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class FileDropClient extends Thread {
    private User user;
    private List<File> files;


    /**
     * Creates the {@code FileDropClient} and calls {@link #start()}.
     *
     * @param user {@link User} the files are uploaded to
     * @param files {@link File} that are uploaded to the {@code user}
     * @since 1.0
     */
    public FileDropClient(User user, List<File> files){
        this.user = user;
        this.files = files;
        start();
    }

    /**
     * Creates a new {@link Socket} and connects to the {@link FileDropServer}. Writes number of files to be send and
     * all filenames and sizes. Then uploads all files in the right order.
     *
     * @since 1.0
     */
    @Override
    public void run() {
        try {
            Socket socket = new Socket(user.getIpAddress(), NetworkHelper.getFileDropPort());
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
