package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerParameterBoolean extends ServerParameter {

    public static final String TRUE = "1",
                                FALSE = "0";

    public ServerParameterBoolean() { }

    public ServerParameterBoolean(String name, String argKey, String argValue){
        super(name, argKey, ServerParameterType.BOOLEAN, argValue);
    }

    public ServerParameterBoolean(JsonNode node){
        this(node.get(NAME).asText(), node.get(ARG).asText(), FALSE);
        if(node.get(STANDARD) != null)
            setArgValue(node.get(STANDARD).asText());
    }

    @Override
    String getParameterConsole() {
        if(getArgValue().equals("Yes") || getArgValue().equals(TRUE))
            return getArgKey() + " " + TRUE;
        else if(getArgValue().equals("No") || getArgValue().equals(FALSE))
            return getArgKey() + " " + FALSE;
        throw new IllegalArgumentException("Wrong argument provided.");
    }

    @Override
    String getParameterWeb() {
        if(getArgValue().equals("Yes") || getArgValue().equals(TRUE))
        return getArgKey() + TRUE;
        else if(getArgValue().equals("No") || getArgValue().equals(FALSE))
            return getArgKey() + FALSE;
        throw new IllegalArgumentException("Wrong argument provided.");
    }

}
