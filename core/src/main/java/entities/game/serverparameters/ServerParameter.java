package entities.game.serverparameters;

public abstract class ServerParameter {

    private String name, argumentBase, standard;
    private ServerParameterType type;

    public ServerParameter() { }

    public ServerParameter(String name, String argumentBase, ServerParameterType type, String standard){
        this.name = name;
        this.argumentBase = argumentBase;
        this.type = type;
        this.standard = standard;
    }

    public String getName(){
        return name;
    }

    public ServerParameterType getType(){
        return type;
    }

    public String getArgumentBase(){
        return argumentBase;
    }

    public abstract String getArgument(String suffix);

    public String getArgument() {
        return getArgument(getStandard());
    }

    public String getStandard(){
        return standard;
    }

    void setStandard(String standard){
        this.standard = standard;
    }

}
