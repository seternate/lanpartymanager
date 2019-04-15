package deserialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import entities.user.User;

import java.io.IOException;
import java.io.StringWriter;

public class UserKeySerializer extends JsonSerializer<User> {
    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public void serialize(User value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, value.getUsername() + ";" + value.getIpAddress() + ";" + value.getGamepath()
                                + ";" + value.getOrder());
        jgen.writeFieldName(writer.toString());
    }

}
