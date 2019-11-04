package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;

public class ServerParameters extends ArrayList<ServerParameter>{

    public ServerParameters() { }

    public ServerParameters(File serverParameters) throws IOException {
        super();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(serverParameters).get("parameters");
        for(int i = 0; i < rootNode.size(); i++){
            JsonNode node = rootNode.get(i);
            ServerParameterType type = ServerParameterType.valueOf(node.get("type").asText().toUpperCase());
            switch(type){
                case NUMBER:    add(new ServerParameterNumber(node)); break;
                case DROPDOWN:  add(new ServerParameterDropdown(node)); break;
                case LITERAL:   add(new ServerParameterLiteral(node)); break;
                case BOOLEAN:   add(new ServerParameterBoolean(node)); break;
                case BASE:      add(new ServerParameterBase(node));
            }
        }
    }

    public String getParameter(){
        StringBuilder parameter = new StringBuilder();
        for(int i = 0; i < size(); i++){
            parameter.append(get(i).getParameter());
            if(get(i).getFormat() == ServerParameterFormat.CONSOLE && i < (size() - 1))
                parameter.append(" ");
            else if(get(i).getFormat() == ServerParameterFormat.WEB && i < (size() - 1))
                parameter.append("?");
        }
        return parameter.toString().trim();
    }

}
