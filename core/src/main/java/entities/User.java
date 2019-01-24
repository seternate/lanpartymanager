package entities;

import helper.PropertiesHelper;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class User {
    private String name;
    private String ip;


    public User(){ }

    public User(boolean create){
        if(!create)
            return;
        this.name = PropertiesHelper.getUsername();
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public User(String name){
        this.name = name;
    }

    public User(String name, String ip){
        this(name);
        this.ip = ip;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getIp(){
        return ip;
    }

    public boolean equals(User user){
        return name.equals(user.getName()) && ip.equals(user.getIp());
    }

    @Override
    public String toString(){
        return name;
    }
}
