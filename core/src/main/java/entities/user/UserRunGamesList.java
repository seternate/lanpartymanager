package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import deserialize.UserKeySerializer;
import deserialize.UserKeyDeserializer;
import entities.game.Game;

import java.util.HashMap;
import java.util.List;

/**
 * {@code UserRunGamesList} handles all running {@link Game}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
@JsonDeserialize(keyUsing = UserKeyDeserializer.class)
@JsonSerialize(keyUsing = UserKeySerializer.class)
public class UserRunGamesList extends HashMap<User, List<Game>> {

    public UserRunGamesList(){ }

}
