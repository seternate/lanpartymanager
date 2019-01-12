package entities;

public final class ServerStatus {
    public boolean serverConnection;
    public String serverIP;

    public ServerStatus(){
        serverConnection = false;
        serverIP = "none";
    }
}
