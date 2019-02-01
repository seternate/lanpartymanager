package entities;

import java.io.*;
import java.net.InetAddress;

public final class User implements UserInterface{
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

    public String getGamepath(){
        return settings.getGamepath();
    }

    public boolean setGamepath(String gamepath){
        if(!getGamepath().equals(gamepath)){
            try {
                settings.setGamepath(gamepath);
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

    public boolean equals(UserInterface user){
        return getUsername().equals(user.getUsername()) && getIpAddress().equals(user.getIpAddress());
    }

    @Override
    public String toString(){
        return getUsername();
    }
}
