package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import deserialize.UserKeySerializer;
import deserialize.UserKeyDeserializer;
import entities.game.Game;

import java.util.HashMap;
import java.util.List;

@JsonDeserialize(keyUsing = UserKeyDeserializer.class)
@JsonSerialize(keyUsing = UserKeySerializer.class)
public class UserRunGamesList extends HashMap<User, List<Game>> {

    public UserRunGamesList(){ }

}
