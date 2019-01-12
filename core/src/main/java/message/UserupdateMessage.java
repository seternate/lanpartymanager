package message;

import entities.User;

public final class UserupdateMessage {
    public User user;


    @SuppressWarnings("unused")
    public UserupdateMessage(){ }

    public UserupdateMessage(User user) {
        this.user = user;
    }
}
