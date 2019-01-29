package entities;

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
        serverConnection = true;
    }

    public void disconnected(){
        serverConnection = false;
    }

    public void setServerIP(String ipAddress){
        serverIP = ipAddress;
    }
}
