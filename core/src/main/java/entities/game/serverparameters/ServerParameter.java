package entities.game.serverparameters;

public abstract class ServerParameter {

    final static String NAME = "name",
                        ARG = "arg",
                        STANDARD = "standard";

    private String name, argKey, argValue;
    private ServerParameterType type;
    private ServerParameterFormat format;

    public ServerParameter() { }

    public ServerParameter(String name, String argKey, ServerParameterType type, String argValue){
        this.name = name;
        this.argKey = argKey;
        this.type = type;
        this.argValue = argValue;
        if(argKey.contains("=") || argKey.isEmpty())
            format = ServerParameterFormat.WEB;
        else
            format = ServerParameterFormat.CONSOLE;
    }

    public String getName(){
        return name;
    }

    public ServerParameterType getType(){
        return type;
    }

    public String getArgKey(){
        return argKey;
    }

    public String getParameter(String argValue){
        if(argValue.isEmpty())
            return getParameter();
        setArgValue(argValue);
        return getParameter();
    }

    abstract String getParameterConsole();

    abstract String getParameterWeb();

    public String getParameter() {
        String parameter = "";
        switch(getFormat()){
            case CONSOLE:   parameter = getParameterConsole(); break;
            case WEB:       parameter = getParameterWeb();
        }
        return parameter;
    }

    public String getArgValue(){
        return argValue;
    }

    public ServerParameterFormat getFormat(){
        return format;
    }

    void setArgValue(String argValue){
        this.argValue = argValue;
    }

}
