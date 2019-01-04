package entities;

public class Status {
    public boolean serverConnection, downloading;
    public String serverIP;

    public Status(){
        serverConnection = false;
        downloading = false;
        serverIP = "none";
    }
}
