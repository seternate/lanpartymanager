package entities.server;

/**
 * Handles the server status.
 */
public class ServerStatus {
    private boolean connected;
    private String serverIP;

    /**
     * Creates the ServerStatus and sets connected to false and ip-address to null.
     */
    public ServerStatus(){
        connected = false;
        serverIP = null;
    }

    /**
     * @return connection status
     */
    public boolean isConnected(){
        return connected;
    }

    /**
     * Sets connection status to true.
     */
    public void connected(){
        connected = true;
    }

    /**
     * Sets connection status to false.
     */
    public void disconnected(){
        connected = false;
    }

    /**
     * @param ipAddress ip-address of the server.
     */
    public void setServerIP(String ipAddress){
        serverIP = ipAddress;
    }

    /**
     * @return server ip-address
     */
    public String getServerIP(){
        return serverIP;
    }

}
