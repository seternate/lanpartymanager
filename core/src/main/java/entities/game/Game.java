package entities.game;

import helper.GameFolderHelper;
import helper.GameInfoHelper;

import java.util.Properties;

/**
 * {@code Game} handles all information about any game.
 * <p>
 *     For the command-line argument for connecting to a server the wildcard '?' is used to be replaced with the
 *     ip-address of the lan-server joining to.
 * </p>
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class Game {

    /**
     * {@code Version} handles the version-information of the {@link Game}.
     *
     * @author Levin Jeck
     * @version 1.0
     * @since 1.0
     */
    public static class Version{
        public String format, file, query;


        /**
         * Creates the {@code Version}.
         *
         * @since 1.0
         */
        public Version(){ }

        /**
         * Creates the {@code Version} with the {@code format}.
         * <p>
         *     If the {@code format} is {@code 'file'}, then the {@code file} is queried with the {@code query} to
         *     retrieve the version-information.
         * </p>
         *
         * @param format one of the following are legal: {@code exe}, {@code file}
         * @param file empty if {@code format} is not {@code file}, else the relative path within the gamefolder of the
         *             {@code Game} has to be given
         * @param query - empty if {@code format} is not {@code file}, else the query within the {@code file} to
         *              determine the version has to be given
         * @since 1.0
         */
        public Version(String format, String file, String query){
            this.format = format;
            this.file = file;
            this.query = query;
        }

        /**
         * @param version {@link Version} from another game, to check if they are equal
         * @return <b>true</b> if this {@code Version} equals {@code version}
         * @since 1.0
         */
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


    private String name, versionServer, connectParam, exeFileRelative, coverUrl, serverFileName, param, exeServerRelative, serverParam;
    private boolean connectDirect, openServer;
    private Version version;


    /**
     * Creates the {@code Game}.
     *
     * @since 1.0
     */
    public Game(){}

    /**
     * Creates the {@code Game} with the given {@code properties}.
     *
     * @since 1.0
     */
    public Game(Properties properties){
        //Copy all properties
        name = properties.getProperty("name");
        versionServer = properties.getProperty("version");
        connectParam = properties.getProperty("connect.param");
        exeFileRelative = parsePath(properties.getProperty("exe.file"));
        coverUrl = properties.getProperty("cover.url");
        serverFileName = properties.getProperty("file.server");
        connectDirect = Boolean.valueOf(properties.getProperty("connect.direct"));
        //Determine and load version-information
        String versionFormat = properties.getProperty("version.format");
        switch(versionFormat){
            case "file": {
                String versionFile = parsePath(properties.getProperty("version.file")),
                        versionQuery = properties.getProperty("version.query");
                version = new Version(versionFormat, versionFile, versionQuery);
                break;
            }
            case "exe": version = new Version(versionFormat, null, null); break;
            default: version = new Version(null, null, null);
        }
        param = properties.getProperty("exe.param")==null ? "" : properties.getProperty("exe.param");
        exeServerRelative = (properties.getProperty("exe.server")==null || properties.getProperty("exe.server").equals(""))
                ? exeFileRelative : parsePath(properties.getProperty("exe.server"));
        serverParam = properties.getProperty("exe.server.param")==null ? "" : properties.getProperty("exe.server.param");
        openServer = Boolean.valueOf(properties.getProperty("openserver"));
    }

    /**
     * Creates the {@code Game} with the given {@code information}.
     * <p>
     *     {@code versionServer} can be empty if no version can be determined from the {@code Game}. The {@code coverUrl}
     *     can be empty if no local coverfile should be used and the cover send from the server will be used.
     * </p>
     *
     * @param name name of the {@code Game}
     * @param versionServer gameversion available on the {@code LANServer}
     * @param connectParam used command-line arguments for joining a lan-server (see {@link #getConnectParam()})
     * @param exeFileRelative relative path of the {@code exe-file} within the gamefolder
     * @param coverUrl absolute path to the coverfile
     * @param serverFileName full filename of the {@code Game} on the {@code LANServer}
     * @param param command-line arguments used to start the {@code Game} and when joining a lan-server
     * @param connectDirect true if there are command-line arguments for joining a lan-server
     * @param version {@link Version} of the {@code Game} locally available
     * @param exeServerRelative relative path of the {@code exe-file} within the gamefolder, if there is any
     * @param serverParam command-line argument used for starting a lan-server
     * @param openServer true if there are command-line arguments for starting a lan-server
     * @since 1.0
     */
    public Game(String name, String versionServer, String connectParam, String exeFileRelative, String coverUrl,
                String serverFileName, String param, boolean connectDirect, Version version, String exeServerRelative,
                String serverParam, boolean openServer){
        this.name = name;
        this.versionServer = versionServer;
        this.connectParam = connectParam;
        this.exeFileRelative = exeFileRelative;
        this.coverUrl = coverUrl;
        this.serverFileName = serverFileName;
        this.connectDirect = connectDirect;
        this.version = version;
        this.param = param;
        this.exeServerRelative = exeServerRelative;
        this.serverParam = serverParam;
        this.openServer = openServer;
    }

    private String parsePath(String relativePath){
        if(relativePath.trim().isEmpty())
            return "";
        else if(relativePath.trim().startsWith("/"))
            return relativePath.trim();
        else
            return "/" + relativePath.trim();
    }

    public String getName(){
        return name;
    }

    public String getServerFileName(){
        return serverFileName;
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

    public String getParam(){
        return param;
    }

    public String getExeServerRelative(){
        return exeServerRelative;
    }

    public String getServerParam(){
        return serverParam;
    }

    public boolean isOpenServer(){
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
                && serverFileName.equals(game.serverFileName) && connectDirect == game.isConnectDirect()
                && version.equals(game.getVersion()) && exeServerRelative.equals(game.getExeServerRelative())
                && serverParam.equals(game.getServerParam()) && param.equals(game.getParam()) && openServer == game.isOpenServer();
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

    @Override
    public String toString(){
        return name;
    }

}
