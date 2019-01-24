package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserList extends HashMap<Integer, User> {

    public List<User> toList(){
        return new ArrayList<>(values());
    }
}
