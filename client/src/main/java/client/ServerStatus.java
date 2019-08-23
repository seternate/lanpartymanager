package client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * {@code ServerStatus} handles the status of the {@code LANServer}. Available information are the connection-status and
 * the {@code LANServer} ip-address.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ServerStatus {
    private BooleanProperty connected;
    private String serverIP;

    /**
     * Creates the {@code ServerStatus} with all fields set to {@code false} or {@code null}.
     *
     * @since 1.0
     */
    public ServerStatus(){
        connected = new SimpleBooleanProperty(false);
        serverIP = null;
    }

    /**
     * @return <b>true</b> if the {@code LANClient} is connected to the {@code LANServer}, else <b>false</b>
     * @since 1.0
     */
    public boolean isConnected(){
        return connected.get();
    }

    /**
     * Sets connection-status to <b>true</b>.
     *
     * @since 1.0
     */
    public void connected(){
        connected.set(true);
    }

    /**
     * Sets connection status to <b>false</b>.
     *
     * @since 1.0
     */
    public void disconnected(){
        connected.set(false);
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

    public BooleanProperty getConnectedProperty(){
        return connected;
    }

}
