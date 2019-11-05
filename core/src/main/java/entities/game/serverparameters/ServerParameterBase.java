package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerParameterBase extends ServerParameter{

    public ServerParameterBase(){ }

    public ServerParameterBase(JsonNode node){
        super(null, node.get(ARG).asText(), ServerParameterType.BASE, null);
    }

    @Override
    String getParameterConsole() {
        return getArgKey();
    }

    @Override
    String getParameterWeb() {
        return getParameterConsole();
    }
}
