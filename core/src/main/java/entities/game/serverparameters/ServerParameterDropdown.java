package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerParameterDropdown extends ServerParameter {

    private Map<String, String> entries;

    public ServerParameterDropdown() { }

    public ServerParameterDropdown(String name, String argumentBase, Map<String, String> entries){
        super(name, argumentBase, ServerParameterType.DROPDOWN, entries.entrySet().iterator().next().getValue());
        this.entries = entries;
    }

    public ServerParameterDropdown(String name, String argumentBase){
        super(name, argumentBase, ServerParameterType.DROPDOWN, null);
        this.entries = new HashMap<>();
    }

    public ServerParameterDropdown(JsonNode node){
        this(node.get("name").asText(), node.get("arg").asText());
        Iterator<Map.Entry<String, JsonNode>> map = node.fields();
        for(int i = 0; i < 3; i++)
            map.next();
        while(map.hasNext()){
            Map.Entry<String, JsonNode> entry = map.next();
            entries.put(entry.getKey(), entry.getValue().asText());
        }
        setStandard(entries.entrySet().iterator().next().getValue());
    }

    @Override
    public String getArgument(String suffix) {
        if(suffix.isEmpty() || !entries.containsKey(suffix))
            return getArgument();
        return getArgumentBase() + " " + entries.get(suffix);
    }

    public String[] getDropdownText(){
        return entries.keySet().toArray(new String[0]);
    }

}
