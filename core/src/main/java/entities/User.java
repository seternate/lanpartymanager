package entities;

import helper.PropertiesHelper;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class User {
    private String name;
    private String gamepath;
    private String ip;


    public User(){
        this.name = PropertiesHelper.getUsername();
        this.gamepath = PropertiesHelper.getGamepath();
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public User(String name, String gamepath){
        this.name = name;
        this.gamepath = gamepath;
    }

    public User(String name, String gamepath, String ip){
        this(name, gamepath);
        this.ip = ip;
    }

    public void print(){
        System.out.println(name);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getGamepath(){
        return gamepath;
    }

    public void setGamepath(String gamepath){
        this.gamepath = gamepath;
    }

    public String getIp(){
        return ip;
    }

    public boolean equals(User user){
        return name.equals(user.getName()) && gamepath.equals(user.getGamepath());
    }
}
