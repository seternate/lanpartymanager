package entities;

import helper.GameFolderHelper;
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

        public boolean equals(Version version){
            if(format == null && file == null && query == null)
                return version.format == null && version.file == null && version.query == null;
            if(format == null && file == null)
                return version.format == null && version.file == null && query.equals(version.query);
            if(format == null && query == null)
                return version.format == null && version.query == null && file.equals(version.file);
            if(file == null && query == null)
                return version.file == null && version.query == null && format.equals(version.format);
            if(format == null)
                return version.format == null && file.equals(version.file) && query.equals(version.query);
            if(file == null)
                return version.file == null && format.equals(version.format) && query.equals(version.query);
            if(query == null)
                return version.query == null && file.equals(version.file) && format.equals(version.format);
            return format.equals(version.format) && file.equals(version.file) && query.equals(version.query);
        }
    }

    private String name, versionServer, connectParam, exeFileRelative, coverUrl, fileServer, param, exeServerRelative, serverParam;
    private boolean connectDirect, openServer;
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
        param = properties.getProperty("exe.param")==null ? "" : properties.getProperty("exe.param");
        exeServerRelative = (properties.getProperty("exe.server")==null || properties.getProperty("exe.server").equals("")) ? exeFileRelative : properties.getProperty("exe.server");
        serverParam = properties.getProperty("exe.server.param")==null ? "" : properties.getProperty("exe.server.param");
        openServer = Boolean.valueOf(properties.getProperty("openserver"));
    }

    public Game(String name, String versionServer, String connectParam, String exeFileRelative, String coverUrl,
                String fileServer,String param, boolean connectDirect, Version version, long sizeServer,
                String exeServerRelative, String serverParam, boolean openServer){
        this.name = name;
        this.versionServer = versionServer;
        this.connectParam = connectParam;
        this.exeFileRelative = exeFileRelative;
        this.coverUrl = coverUrl;
        this.fileServer = fileServer;
        this.connectDirect = connectDirect;
        this.version = version;
        this.sizeServer = sizeServer;
        this.param = param;
        this.exeServerRelative = exeServerRelative;
        this.serverParam = serverParam;
        this.openServer = openServer;
    }

    public void print(){
        System.out.println("Name: " + name);
        System.out.println("Version: " + versionServer);
        System.out.println("Size: " + sizeServer);
        System.out.println("Filename Server: " + fileServer);
        System.out.println("Server Exe: " + exeServerRelative + "\n");
    }

    public String getName(){
        return name;
    }

    public String getFileServer(){
        return fileServer;
    }

    public String getVersionServer(){
        return versionServer;
    }

    public String getCoverUrl(){
        return this.coverUrl;
    }

    public Version getVersion(){
        return version;
    }

    public String getExeFileRelative() {
        return exeFileRelative;
    }

    public String getConnectParam(){
        return connectParam;
    }

    public boolean isConnectDirect(){
        return connectDirect;
    }

    public long getSizeServer(){
        return sizeServer;
    }

    public String getParam(){
        return param;
    }

    public String getExeServerRelative(){
        return exeServerRelative;
    }

    public String getServerParam(){
        return serverParam;
    }

    public boolean getOpenServer(){
        return openServer;
    }

    public int isUptodate(){
        if(GameFolderHelper.getAbsolutePath(exeFileRelative) == null)
            return -1;
        else if(versionServer.equals(""))
            return -2;
        else if(!versionServer.equals(getLocalVersion()))
            return -3;
        else
            return 0;
    }

    public boolean equals(Game game){
        return name.equals(game.getName()) && versionServer.equals(game.getVersionServer()) && connectParam.equals(game.getConnectParam())
                && exeFileRelative.equals(game.getExeFileRelative()) && coverUrl.equals(game.getCoverUrl())
                && fileServer.equals(game.fileServer) && connectDirect == game.isConnectDirect()
                && version.equals(game.getVersion()) && sizeServer == game.getSizeServer() && exeServerRelative.equals(game.getExeServerRelative())
                && serverParam.equals(game.getServerParam()) && param.equals(game.getParam()) && openServer == game.getOpenServer();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Game)
            return equals((Game)o);
        return super.equals(o);
    }

    private String getLocalVersion(){
        return GameInfoHelper.getVersion(this);
    }

}
