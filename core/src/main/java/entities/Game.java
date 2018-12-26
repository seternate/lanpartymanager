package entities;

import helper.GameInfoHelper;

public class Game {

    private String name,
                   version,
                   exePath,
                   folderPath;

    public Game(String folderPath, String exePathR){
        this.folderPath = folderPath;
        this.exePath = folderPath + exePathR;
        this.name = GameInfoHelper.getFileInfo(exePath)[1];
        this.version = GameInfoHelper.getFileInfo(exePath)[0];

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

    public String getFolderPath() {
        return folderPath;
    }



}
