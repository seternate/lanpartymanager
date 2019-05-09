package entities.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@code UserList} manages {@link User}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class UserList extends HashMap<Integer, User> {

    /**
     * Creates the {@code UserList}.
     *
     * @since 1.0
     */
    public UserList(){ }

    /**
     * Creates the {@code UserList} as a copy of {@code userlist}.
     *
     * @param userlist {@link UserList} to copy
     * @since 1.0
     */
    public UserList(UserList userlist){
        super(userlist);
    }

    /**
     * @return {@code List} of all {@link User}
     * @since 1.0
     */
    public List<User> toList(){
        return new ArrayList<>(values());
    }

    /**
     * Checks if this {@code UserList} has the same {@code users} as the {@code userlist}.
     *
     * @param userlist {@code UserList} to compare with
     * @return <b>true</b> if this {@code UserList} has the same {@code users} as {@code userlist}, else <b>false</b>
     * @since 1.0
     */
    public boolean equals(UserList userlist){
        //Check if the size are same
        if(toList().size() != userlist.toList().size())
            return false;
        //Check every user and find the same in the other userlist
        for(User user : toList()){
            for(int k = 0; k < userlist.toList().size(); k++){
                if(!user.equals(userlist.toList().get(k)) && k == userlist.toList().size() - 1){
                    return false;
                }else if(user.equals(userlist.toList().get(k))){
                    break;
                }
            }
        }
        return true;
    }

    /**
     * Removes the {@code user} from the {@code UserList}.
     *
     * @param user {@link User} to remove from the {@code UserList}
     * @since 1.0
     */
    public void remove(User user){
        this.keySet().removeIf(e -> get(e).equals(user));
    }

    /**
     *
     * @param user {@link User} from the {@code UserList}
     * @return connectionID within the {@code KryoNet} from the {@code user} or <b>-1</b> if the {@code user} can not be
     *          found in the {@code UserList}
     * @since 1.0
     */
    public int getConnectionID(User user){
        for(Integer key : this.keySet()){
            if(get(key).equals(user))
                return key;
        }
        return -1;
    }

}
