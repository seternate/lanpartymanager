package entities;

import java.io.*;
import java.net.InetAddress;

public final class User {
    private ClientSettings settings;
    private String ipAddress;


    public User(){ }

    public User(ClientSettings settings) throws IOException {
        this.settings = settings;
        ipAddress = InetAddress.getLocalHost().getHostAddress();
    }

    public String getUsername(){
        return settings.getUsername();
    }

    public boolean setUsername(String username){
        if(!getUsername().equals(username)){
            try {
                settings.setUsername(username);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getIpAddress(){
        return ipAddress;
    }

    public boolean equals(User user){
        return getUsername().equals(user.getUsername()) && ipAddress.equals(user.getIpAddress());
    }

    @Override
    public String toString(){
        return getUsername();
    }
}
