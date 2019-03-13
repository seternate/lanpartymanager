package entities;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown = true)
public final class ServerStatus {
    private boolean connected;
    private String serverIP;

    public ServerStatus(){
        connected = false;
        serverIP = null;
    }

    public boolean isConnected(){
        return connected;
    }

    public void connected(){
        setConnected(true);
    }

    public void disconnected(){
        setConnected(false);
    }

    public void setServerIP(String ipAddress){
        serverIP = ipAddress;
    }

    public String getServerIP(){
        return serverIP;
    }

    public void setConnected(boolean connection){
        connected = connection;
    }
}
