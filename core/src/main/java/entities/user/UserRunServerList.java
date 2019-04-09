package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import deserialize.UserKeyDeserializer;
import deserialize.UserKeySerializer;
import entities.game.Game;

import java.util.HashMap;
import java.util.List;

@JsonDeserialize(keyUsing = UserKeyDeserializer.class)
@JsonSerialize(keyUsing = UserKeySerializer.class)
public class UserRunServerList extends HashMap<User, List<Game>> {

    public UserRunServerList(){ }

}
