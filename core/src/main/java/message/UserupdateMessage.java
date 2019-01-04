package message;

import entities.User;

public class UserupdateMessage {
    public User user;


    public UserupdateMessage(){ }

    public UserupdateMessage(User user) {
        this.user = user;
    }
}
