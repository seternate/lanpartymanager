package deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import entities.settings.ClientSettings;
import entities.user.User;

import java.io.IOException;

/**
 * {@code UserKeyDeserializer} is used for the deserialization of the {@link User} as a {@code Key} in
 * {@link java.util.Map}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @see UserKeySerializer
 * @since 1.0
 */
public final class UserKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        //Remove the starting and trailing quotation marks
        key = key.substring(1, key.length() - 1);
        //Split the User information
        String[] userfields = key.split(";");
        //Create a new User
        User user = new User(new ClientSettings());
        //Add all information to the User
        user.setUsername(userfields[0]);
        user.setIpAddress(userfields[1]);
        user.setGamepath(userfields[2]);
        if(userfields.length == 4)
            user.setOrder(userfields[3]);
        return user;
    }

}
