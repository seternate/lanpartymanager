package message;

import entities.User;

public class LoginMessage {
    public User user;


    public LoginMessage(){ }

    public LoginMessage(User user){
        this.user = user;
    }
}
