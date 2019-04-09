package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import deserialize.UserKeySerializer;
import deserialize.UserRunGamesListDeserializer;
import entities.game.Game;

import java.util.HashMap;
import java.util.List;

@JsonDeserialize(keyUsing = UserRunGamesListDeserializer.class)
@JsonSerialize(keyUsing = UserKeySerializer.class)
public class UserRunGamesList extends HashMap<User, List<Game>> {

    public UserRunGamesList(){ }


}
