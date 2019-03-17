package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class UserList extends HashMap<Integer, User> {

    public UserList(){ }

    public UserList(UserList userlist){
        super(userlist);
    }

    public List<User> toList(){
        return new ArrayList<>(values());
    }

    public boolean equals(UserList userlist){
        if(toList().size() != userlist.toList().size())
            return false;
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

    public void remove(User user){
        this.keySet().removeIf(e -> get(e).equals(user));
    }
}
