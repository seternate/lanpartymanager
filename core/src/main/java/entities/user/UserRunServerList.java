package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import deserialize.UserKeyDeserializer;
import deserialize.UserKeySerializer;
import entities.game.Game;

import java.util.HashMap;
import java.util.List;

/**
 * {@code UserRunServerList} handles all running servers from any {@link Game}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
@JsonDeserialize(keyUsing = UserKeyDeserializer.class)
@JsonSerialize(keyUsing = UserKeySerializer.class)
public class UserRunServerList extends HashMap<User, List<Game>> {

    public UserRunServerList(){ }

}
