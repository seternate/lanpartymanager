package helper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Helper class to find any free port on the system.
 */
public abstract class NetworkHelper {
    private static Logger log = Logger.getLogger(NetworkHelper.class);
    private static int filedropport = 1337;


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

    public static String getIPAddress() throws UnknownHostException {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(interfaces)) {
                if(!netint.isLoopback() && netint.isUp() && !netint.isVirtual()){
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        if(inetAddress.getHostAddress().contains("192.168.0."))
                            return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("I/O error while detecting ip address of the host system", e);
        }
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static int getFileDropPort(){
        return filedropport;
    }

}
