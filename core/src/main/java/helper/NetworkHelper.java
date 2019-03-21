package helper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Helper class to find any free port on the system.
 */
public abstract class NetworkHelper {
    private static Logger log = Logger.getLogger(NetworkHelper.class);


    /**
     * @return a free port of the system or -1 on any failure.
     */
    public static int getOpenPort(){
        ServerSocket socket;
        //Try opening a socket with a random port
        try {
            socket = new ServerSocket(0);
        } catch (IOException e) {
            log.error("Can not open a new socket to find a free port.", e);
            return -1;
        }
        //Read the port of the socket
        int port = socket.getLocalPort();
        //Close the socket
        try {
            socket.close();
        } catch (IOException e) {
            log.warn("Can not close the socket.", e);
        }
        //Return free port
        return port;
    }

    public static String getIPAddress(){
        //TODO
        return null;
    }

}
