package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ServerStatus {
    private boolean serverConnection;
    private String serverIP;


    public ServerStatus(){
        serverConnection = false;
        serverIP = null;
    }

    public boolean isConnected(){
        return serverConnection;
    }

    public void connected(){
        setServerConnection(true);
    }

    public void disconnected(){
        setServerConnection(false);
    }

    public void setServerIP(String ipAddress){
        serverIP = ipAddress;
    }

    public void setServerConnection(boolean connection){
        serverConnection = connection;
    }
}
