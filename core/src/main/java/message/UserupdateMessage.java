package message;

import entities.user.User;

/**
 * {@code UserUpdateMessage} is a class to notify the {@code LANServer} that a {@link User} has updated his information.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class UserupdateMessage {
    public User user;

    public UserupdateMessage(){ }

    public UserupdateMessage(User user) {
        this.user = user;
    }

}
