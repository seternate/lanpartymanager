package entities;

import helper.GameInfoHelper;
import helper.PropertiesHelper;

import java.util.Properties;

public class Game {

    private String name,
                   version,
                   connectParam,
                   exePath;
    private boolean connectDirect;

    public Game(Properties properties){
        this.name = properties.getProperty("name");
        this.exePath = properties.getProperty("exe.path");
        switch (properties.getProperty("version.format")){
            case "file": GameInfoHelper.fileVersion(properties.getProperty("version.path"), properties.getProperty("version.query"));break;
            case "date": GameInfoHelper.dateVersion(properties.getProperty("exe.path"));break;
            default: GameInfoHelper.getVersion(properties.getProperty("exe.path"));break;
        }
        if(Boolean.getBoolean(properties.getProperty("connect.direct"))){
            connectDirect = true;
            connectParam = properties.getProperty("connect.param");
        }else{
            connectDirect = false;
            connectParam = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getExePath() {
        return exePath;
    }
}
