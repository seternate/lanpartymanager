package entities;

import helper.PropertiesHelper;

public class User {

    private String name;

    public User(){
        this.name = PropertiesHelper.getUsername();
    }

    @Override
    public String toString(){
        return name;
    }
}
