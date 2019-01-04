package message;

import entities.User;

import java.util.Map;

public class UserlistMessage {
    public Map<Integer, User> users;


    public UserlistMessage(){ }

    public UserlistMessage(Map<Integer, User> users){
        this.users = users;
    }
}
