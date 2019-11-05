package entities.game.serverparameters;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerParameterNumber extends ServerParameter {
    private static String LOWER_BOUND = "lowerbound",
                          UPPER_BOUND = "upperbound";

    private double lowerBound, upperBound;

    public ServerParameterNumber() { }

    public ServerParameterNumber(JsonNode node){
        this(node.get(NAME).asText(), node.get(ARG).asText(), node.get(STANDARD).asText(),
                node.get(LOWER_BOUND).asDouble(), node.get(UPPER_BOUND).asDouble());
    }

    public ServerParameterNumber(String name, String argKey, String argValue, double lowerBound,
                                 double upperBound){
        super(name, argKey, ServerParameterType.NUMBER, argValue);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String getParameter() {
        double suffixNumber = Double.parseDouble(getArgValue());
        if(suffixNumber < lowerBound || suffixNumber > upperBound)
            throw new IllegalArgumentException("Suffix is out of bounds. Lower: " + lowerBound + "; Upper: " + upperBound);
        return super.getParameter();
    }

    @Override
    String getParameterConsole() {
        return getArgKey() + " " + getArgValue();
    }

    @Override
    String getParameterWeb() {
        return getArgKey() + getArgValue();
    }

}
