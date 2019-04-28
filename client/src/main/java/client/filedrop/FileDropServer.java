package client.filedrop;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code FileDropServer} handles the download of any files from another {@code LANClient} logged in the
 * {@code LANServer}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class FileDropServer extends Thread {
    private ServerSocket ss;
    private Socket socket;
    private boolean downloading;
    private String path;

    /**
     * Creates the {@code FileDropServer} and calls {@link #start()}.
     *
     * @param path absolute {@code path} where the files are downloaded and saved
     * @since 1.0
     */
    public FileDropServer(String path){
        downloading = false;
        this.path = path;
        try {
            ss = new ServerSocket(1337);
            socket = new Socket();
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to accept incoming files. Then downloads and saves it.
     *
     * @since 1.0
     */
    @Override
    public void run() {
        while(true){
            try{
                if(!socket.isConnected()){
                    socket = ss.accept();
                    downloading = true;
                    saveFiles();
                    socket = new Socket();
                } else
                    sleep(10);
            } catch (Exception e) {
                downloading = false;
                e.printStackTrace();
            } finally {
                downloading = false;
            }
        }
    }

    /**
     * Reads the number of files to download and the filename and size. Then download and saves it.
     *
     * @throws IOException if any IO-Error occurs
     * @since 1.0
     */
    private void saveFiles() throws IOException {
        DataInputStream data = new DataInputStream(socket.getInputStream());
        BufferedInputStream buffered = new BufferedInputStream(socket.getInputStream());
        //Read number of files to be received
        int fileCount = data.readInt();
        //Read filenames and filesizes
        List<File> files = new ArrayList<>(fileCount);
        List<Integer> filesizes = new ArrayList<>(fileCount);
        for(int i = 0; i < fileCount; i++){
            File file = new File(data.readUTF());
            files.add(file);
            filesizes.add(data.readInt());
        }
        //Read files
        for(int i = 0; i < fileCount; i++){
            FileOutputStream fos = new FileOutputStream(path + files.get(i), false);
            DataOutputStream dos = new DataOutputStream(fos);
            byte[] buffer = new byte[1048576];
            int filesize = filesizes.get(i);

            int read;
            while((read = data.read(buffer, 0, Math.min(buffer.length, filesize))) > 0){
                filesize -= read;
                fos.write(buffer, 0, read);
            }

            dos.close();
            fos.close();
        }
        //Close all streams
        buffered.close();
        data.close();
    }

    /**
     * @return <b>true</b> if any files are downloaded, else <b>false</b>
     * @since 1.0
     */
    public boolean isDownloading(){
        return downloading;
    }

}
