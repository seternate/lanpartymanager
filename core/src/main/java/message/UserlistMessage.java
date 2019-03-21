package message;

import entities.user.UserList;

public final class UserlistMessage {
    public UserList users;


    @SuppressWarnings("unused")
    public UserlistMessage(){ }

    public UserlistMessage(UserList users){
        this.users = users;
    }
}
