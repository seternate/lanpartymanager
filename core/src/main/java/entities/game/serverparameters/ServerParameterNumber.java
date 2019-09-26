package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerParameterNumber extends ServerParameter {
    private int lowerBound, upperBound;

    public ServerParameterNumber() { }

    public ServerParameterNumber(String name, String argumentBase, String standard, int lowerBound,
                                 int upperBound){
        super(name, argumentBase, ServerParameterType.NUMBER, standard);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public ServerParameterNumber(JsonNode node){
        this(node.get("name").asText(), node.get("arg").asText(), node.get("standard").asText(),
                node.get("lowerbound").asInt(), node.get("upperbound").asInt());
    }

    @Override
    public String getArgument(String suffix) {
        int suffixNumber = Integer.parseInt(suffix);
        if(suffixNumber < lowerBound || suffixNumber > upperBound)
            throw new IllegalArgumentException("Suffix is out of bounds. Lower: " + lowerBound + "; Upper: " + upperBound);
        return getArgumentBase() + " " + suffix;
    }

}
