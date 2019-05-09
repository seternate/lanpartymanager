package message;

import entities.user.User;

/**
 * {@code LoginMessage} is a class to notify the {@code LANServer} that a {@link User} attempts to log in.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class LoginMessage {
    public User user;

    public LoginMessage(){ }

    public LoginMessage(User user){
        this.user = user;
    }

}
