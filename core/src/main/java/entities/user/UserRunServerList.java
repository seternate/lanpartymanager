package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import deserialize.UserRunServerListDeserializer;
import entities.game.Game;

import java.util.HashMap;
import java.util.List;

@JsonDeserialize(keyUsing = UserRunServerListDeserializer.class)
public class UserRunServerList extends HashMap<User, List<Game>> { }
