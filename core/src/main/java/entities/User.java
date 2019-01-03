package entities;

import helper.PropertiesHelper;

public class User {

    private String name;

    public User(){
        this.name = PropertiesHelper.getUsername();
    }

    public User(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
