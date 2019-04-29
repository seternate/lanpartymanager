package deserialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import entities.user.User;

import java.io.IOException;
import java.io.StringWriter;

/**
 * {@code UserKeySerializer} is used for the serialization of the {@link User} as a {@code Key} in
 * {@link java.util.Map}.
 * <p>
 *     It serializes the {@code User} as following {@code String}: '[username];[ipAddress];[gamepath];[order]'
 * </p>
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class UserKeySerializer extends JsonSerializer<User> {
    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public void serialize(User value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, value.getUsername() + ";" + value.getIpAddress() + ";" + value.getGamepath()
                                + ";" + value.getOrder());
        jgen.writeFieldName(writer.toString());
    }

}
