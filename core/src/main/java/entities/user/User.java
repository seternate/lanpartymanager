package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import deserialize.UserDeserializer;
import entities.settings.ClientSettings;
import helper.NetworkHelper;

import java.io.*;
import java.net.UnknownHostException;

@JsonDeserialize(using = UserDeserializer.class)
public final class User {
    private ClientSettings settings;
    private String ipAddress;
    private String order;


    public User(){ }

    public User(ClientSettings settings) throws UnknownHostException {
        this.settings = settings;
        ipAddress = NetworkHelper.getIPAddress();
        order = "";
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

    public String getOrder(){
        return order;
    }

    public void setOrder(String order){
        this.order = order;
    }

    public boolean update(User user) throws IOException {
        if(!this.equals(user)){
            if(!user.getUsername().isEmpty())
                setUsername(user.getUsername());
            if(!user.getGamepath().isEmpty())
                setGamepath(user.getGamepath());
            if(!user.getIpAddress().isEmpty())
                setIpAddress(user.getIpAddress());
            setOrder(user.getOrder());
        }else{
            return false;
        }
        settings.save();
        return true;
    }

    public boolean equals(User user){
        return getUsername().equals(user.getUsername()) && getGamepath().equals(user.getGamepath())
                && getIpAddress().equals(user.getIpAddress())
                && getOrder().equals(user.getOrder());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof User)
            return equals((User)o);
        return super.equals(o);
    }

    @Override
    public int hashCode(){
        return getUsername().hashCode() + getGamepath().hashCode() + getIpAddress().hashCode() + getOrder().hashCode();
    }

    @Override
    public String toString(){
        return getUsername();
    }

}
