package message;

import entities.User;

import java.util.Map;

public final class UserlistMessage {
    public Map<Integer, User> users;


    @SuppressWarnings("unused")
    public UserlistMessage(){ }

    public UserlistMessage(Map<Integer, User> users){
        this.users = users;
    }
}
