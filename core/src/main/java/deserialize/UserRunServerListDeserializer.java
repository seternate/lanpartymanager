package deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import entities.game.Game;
import entities.user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserRunServerListDeserializer extends KeyDeserializer {
    
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return null;
    }

}
