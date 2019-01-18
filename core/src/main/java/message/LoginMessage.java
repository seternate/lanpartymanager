package message;

import entities.User;

public final class LoginMessage {
    public User user;


    @SuppressWarnings("unused")
    public LoginMessage(){ }

    public LoginMessage(User user){
        this.user = user;
    }
}
