package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerParameterLiteral extends ServerParameter {

    public ServerParameterLiteral() { }

    public ServerParameterLiteral(String name, String argumentBase, String standard){
        super(name, argumentBase, ServerParameterType.LITERAL, standard);
    }

    public ServerParameterLiteral(JsonNode node){
        this(node.get("name").asText(), node.get("arg").asText(), node.get("standard").asText());
    }

    @Override
    public String getArgument(String suffix) {
        if(suffix.isEmpty())
            return getArgument();
        return getArgumentBase() + " \"" + suffix + "\"";
    }
}
