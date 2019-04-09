package deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import entities.settings.ClientSettings;
import entities.user.User;

import java.io.IOException;

public final class UserRunGamesListDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        key = key.substring(1, key.length() - 1);
        String[] userfields = key.split(";");
        User user = new User(new ClientSettings());
        user.setUsername(userfields[0]);
        user.setIpAddress(userfields[1]);
        user.setGamepath(userfields[2]);
        if(userfields.length == 4)
            user.setOrder(userfields[3]);
        return user;
    }

}
