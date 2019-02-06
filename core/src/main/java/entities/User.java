package entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import deserializer.UserDeserializer;

import java.io.*;
import java.net.InetAddress;

@JsonDeserialize(using = UserDeserializer.class)
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

    public void setUsername(String username){
        settings.setUsername(username);
    }

    public String getGamepath(){
        return settings.getGamepath();
    }

    public void setGamepath(String gamepath){
        settings.setGamepath(gamepath);
    }

    public String getIpAddress(){
        return ipAddress;
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public boolean update(User user) throws IOException {
        if(!this.equals(user)){
            if(!user.getUsername().isEmpty())
                setUsername(user.getUsername());
            else
                return false;
            if(!user.getGamepath().isEmpty())
                setGamepath(user.getGamepath());
            else
                return false;
            if(!user.getIpAddress().isEmpty())
                setIpAddress(user.getIpAddress());
            else
                return false;
        }else{
            return false;
        }
        settings.save();
        return true;
    }

    public boolean equals(User user){
        return getUsername().equals(user.getUsername()) && getGamepath().equals(user.getGamepath()) && getIpAddress().equals(user.getIpAddress());
    }

    @Override
    public String toString(){
        return getUsername();
    }
}
