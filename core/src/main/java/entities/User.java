package entities;

import helper.PropertiesHelper;

public class User {
    private String name;
    private String gamepath;


    public User(){
        this.name = PropertiesHelper.getUsername();
        this.gamepath = PropertiesHelper.getGamepath();
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
}
