package deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import entities.settings.ClientSettings;
import entities.user.User;

import java.io.IOException;

/**
 * {@code UserDeserializer} is used for the deserialization of the {@link User}.
 * <p>
 *     It handles the complex structure behind the {@code User} creation.
 * </p>
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer() {
        this(null);
    }

    protected UserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        //Extract the Json
        JsonNode node = jp.getCodec().readTree(jp);
        //Get all important information from the Json
        String username = node.get("username").asText();
        String gamepath = node.get("gamepath").asText();
        String ipaddress = node.get("ipAddress").asText();
        String order = node.get("order").asText();
        //Create the ClientSettings
        ClientSettings settings = new ClientSettings();
        settings.setUsername(username);
        settings.setGamepath(gamepath);
        //Create the User with the ClientSettings and the information from the Json
        User user = new User(settings);
        user.setIpAddress(ipaddress);
        user.setOrder(order);
        return user;
    }

}
