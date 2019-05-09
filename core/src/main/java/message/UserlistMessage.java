package message;

import entities.user.UserList;

/**
 * {@code UserListMessage} is a class to send a {@link UserList}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class UserlistMessage {
    public UserList users;

    public UserlistMessage(){ }

    public UserlistMessage(UserList users){
        this.users = users;
    }

}
