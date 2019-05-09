package helper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * {@code NetworkHelper} is a helper class to find free ports, the ip-address and the filedrop port of the system.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public abstract class NetworkHelper {
    private static Logger log = Logger.getLogger(NetworkHelper.class);
    private static final int filedropport = 1337;


    /**
     * @return <b>-1</b> if no free port was found, else a free port
     * @since 1.0
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

    /**
     * Filters out any local ip-addresses, which causes problems to find {@code gameservers} (e.g. Virtual Machine).
     *
     * @return local ip-address of the system
     * @throws UnknownHostException if any error occurs
     * @since 1.0
     */
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

    /**
     * @return filedrop port of {@value filedropport}
     * @since 1.0
     */
    public static int getFileDropPort(){
        return filedropport;
    }

}
