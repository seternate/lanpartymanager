package deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import entities.ClientSettings;
import entities.User;

import java.io.IOException;

public final class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer() {
        this(null);
    }

    protected UserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String username = node.get("username").asText();
        String gamepath = node.get("gamepath").asText();
        String ipaddress = node.get("ipAddress").asText();
        String order = node.get("order").asText();

        ClientSettings settings = new ClientSettings();
        settings.setUsername(username);
        settings.setGamepath(gamepath);

        User user = new User(settings);
        user.setIpAddress(ipaddress);
        user.setOrder(order);

        return user;
    }
}
