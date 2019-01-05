package entities;

import helper.GameInfoHelper;

import java.util.Properties;

public final class Game {
    public static class Version{
        public String format, file, query;


        public Version(){ }

        public Version(String format, String file, String query){
            this.format = format;
            this.file = file;
            this.query = query;
        }
    }

    private String name, versionServer, connectParam, exeFileRelative, coverUrl, fileServer;
    private boolean connectDirect;
    private Version version;
    private long sizeServer;


    public Game(){}

    public Game(Properties properties){
        name = properties.getProperty("name");
        versionServer = properties.getProperty("version");
        connectParam = properties.getProperty("connect.param");
        exeFileRelative = properties.getProperty("exe.file");
        coverUrl = properties.getProperty("cover.url");
        fileServer = properties.getProperty("file.server");
        connectDirect = Boolean.valueOf(properties.getProperty("connect.direct"));
        String versionFormat = properties.getProperty("version.format");
        switch(versionFormat){
            case "file": {
                String versionFile = properties.getProperty("version.file"),
                        versionQuery = properties.getProperty("version.query");
                version = new Version(versionFormat, versionFile, versionQuery);
                break;
            }
            case "exe": version = new Version(versionFormat, null, null); break;
            default: version = new Version(null, null, null);
        }
        sizeServer = Long.valueOf(properties.getProperty("file.server.size"));
    }

    public void print(){
        System.out.println("Name: " + name);
        System.out.println("Version: " + versionServer);
        System.out.println("Size: " + sizeServer);
        System.out.println("Filename Server: " + fileServer + "\n");
    }

    public String getName(){
        return name;
    }

    public String getFileserver(){
        return fileServer;
    }

    public String getVersionServer(){
        return versionServer;
    }

    public boolean isUptodate(){
        return versionServer.equals(GameInfoHelper.getVersion());
    }


/*

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getExeFileRelative() {
        return exeFileRelative;
    }

    public Properties getProperties(){
        return this.properties;
    }

    public long getSize(){
        return gamesize;
    }

    public boolean isConnectDirect(){
        return connectDirect;
    }

    public String getConnectParam(){
        return connectParam;
    }

    public String getConnectParam(String ip){
        if(!isConnectDirect()) return getConnectParam();
        String cParam = getConnectParam();
        return cParam.replace("?", ip);
    }

    public String getPosterUrl(){
        return this.posterUrl;
    }

    public String getLocalVersion(){
        return GameInfoHelper.getVersion(this);
    }

    public boolean isUpToDate(){
        return this.version.equals(getLocalVersion());
    }
    */
}
