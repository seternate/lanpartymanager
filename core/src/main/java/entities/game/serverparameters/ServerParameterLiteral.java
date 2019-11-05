package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerParameterLiteral extends ServerParameter {

    public ServerParameterLiteral() { }

    public ServerParameterLiteral(JsonNode node){
        this(node.get(NAME).asText(), node.get(ARG).asText(), node.get(STANDARD).asText());
    }

    public ServerParameterLiteral(String name, String argKey, String argValue){
        super(name, argKey, ServerParameterType.LITERAL, argValue);
    }

    @Override
    String getParameterWeb(){
        return getArgKey() + "\"" + getArgValue() + "\"";
    }

    @Override
    String getParameterConsole(){
        return getArgKey() + " \"" + getArgValue() + "\"";
    }

}
