package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DragAndDropServer extends Thread {
    private ServerSocket ss;
    private Socket socket;

    public DragAndDropServer(){
        try {
            ss = new ServerSocket(1337);
            socket = new Socket();
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try{
                if(!socket.isConnected()){
                    socket = ss.accept();
                    saveFiles();
                } else {
                    sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

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
            FileOutputStream fos = new FileOutputStream(files.get(i), false);
            DataOutputStream dos = new DataOutputStream(fos);
            //BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[1048576];
            int filesize = filesizes.get(i);

            int read = 0;
            while((read = data.read(buffer, 0, Math.min(buffer.length, filesize))) > 0){
                filesize -= read;
                fos.write(buffer, 0, read);
                //fos.flush();
            }

            //bos.close();
            dos.close();
            fos.close();
        }








        buffered.close();
        data.close();
    }
}
