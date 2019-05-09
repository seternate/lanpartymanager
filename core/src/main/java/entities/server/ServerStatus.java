package entities.server;

/**
 * {@code ServerStatus} handles the status of the {@code LANServer}. Available information are the connection-status and
 * the {@code LANServer} ip-address.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ServerStatus {
    private boolean connected;
    private String serverIP;

    /**
     * Creates the {@code ServerStatus} with all fields set to {@code false} or {@code null}.
     *
     * @since 1.0
     */
    public ServerStatus(){
        connected = false;
        serverIP = null;
    }

    /**
     * @return <b>true</b> if the {@code LANClient} is connected to the {@code LANServer}, else <b>false</b>
     * @since 1.0
     */
    public boolean isConnected(){
        return connected;
    }

    /**
     * Sets connection-status to <b>true</b>.
     *
     * @since 1.0
     */
    public void connected(){
        connected = true;
    }

    /**
     * Sets connection status to <b>false</b>.
     *
     * @since 1.0
     */
    public void disconnected(){
        connected = false;
    }

    /**
     * @param ipAddress ip-address of the current connected {@code LANServer}
     * @since 1.0
     */
    public void setServerIP(String ipAddress){
        serverIP = ipAddress;
    }

    /**
     * @return ip-address of the current or last connected {@code LANServer}
     * @since 1.0
     */
    public String getServerIP(){
        return serverIP;
    }

}
