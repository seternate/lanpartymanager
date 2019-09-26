package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerParameterBoolean extends ServerParameter {

    public ServerParameterBoolean() { }

    public ServerParameterBoolean(String name, String argumentBase){
        super(name, argumentBase, ServerParameterType.BOOLEAN, "1");
    }

    public ServerParameterBoolean(JsonNode node){
        this(node.get("name").asText(), node.get("arg").asText());
    }

    @Override
    public String getArgument(String suffix) {
        if(suffix.equals("Yes"))
            return getArgumentBase() + " 1";
        else if(suffix.equals("No"))
            return getArgumentBase() + " 0";
        throw new IllegalArgumentException("Wrong argument provided.");
    }

}
