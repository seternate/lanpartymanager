package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerParameterDropdown extends ServerParameter {

    private Map<String, String> entries;
    private ArrayList<String> dropdownText;

    public ServerParameterDropdown() { }

    public ServerParameterDropdown(JsonNode node){
        this(node.get(NAME).asText(), node.get(ARG).asText());
        Iterator<Map.Entry<String, JsonNode>> map = node.fields();
        dropdownText = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            map.next();
        while(map.hasNext()){
            Map.Entry<String, JsonNode> entry = map.next();
            entries.put(entry.getKey(), entry.getValue().asText());
            dropdownText.add(entry.getKey());
        }
        setArgValue(entries.get(dropdownText.get(0)));
    }

    public ServerParameterDropdown(String name, String argKey, Map<String, String> entries){
        super(name, argKey, ServerParameterType.DROPDOWN, entries.entrySet().iterator().next().getValue());
        this.entries = entries;
        dropdownText = new ArrayList<>();
        entries.forEach((key,value) -> {
            dropdownText.add(key);
        });
    }

    public ServerParameterDropdown(String name, String argKey){
        super(name, argKey, ServerParameterType.DROPDOWN, null);
        this.entries = new HashMap<>();
    }

    @Override
    String getParameterConsole() {
        return getArgKey() + " " + entries.get(getArgValue());
    }

    @Override
    String getParameterWeb() {
        return getArgKey() + entries.get(getArgValue());
    }

    public ArrayList<String> getDropdownText(){
        return dropdownText;
    }

}
