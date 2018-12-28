package entities;

import helper.GameInfoHelper;

import java.util.Properties;

/**
 * {@link Game} class holding all required information of a game.
 * <p>
 * To create a {@link Game} object a proper {@link Properties} file is needed. The {@link Properties} file has the
 * following fields:
 * <p>
 * <code>exe.file</code> - the relative <code>.exe</code>-path without the root game-folder.
 * <p>
 * <code>name</code> - the name of the game.
 * <p>
 * <code>version.format</code> - the format, in which the version of the game is provided. (<code>file,date,
 * exe</code>)
 * <p>
 * <code>version.file</code> - the relative path of file, in which the version is written without the root game-folder,
 * if <code>version.format=file</code>. Else this field is empty.
 * <p>
 * <code>version.query</code> - a query to find the version in <code>version.file</code>, if <code>version.format=file
 * </code>. Else this field is empty.
 * <p>
 * <code>connect.direct</code> - boolean value to to determine if the game supports direct connecting to the game-
 * server.
 * <p>
 * <code>connect.param</code> - the parameter, which is used for direct connecting to a server, if <code>connect.direct=
 * true</code>. Else this field is empty. <code>?</code> is the placeholder for the ip of the server.
 * <p><p>
 * <code>Relative path - Example:</code><br>
 * <pre>
 *      <code>Absolute path: C:\Program Files (x86)\games\Teeworlds\teeworlds.exe</code>
 *      <code>Relative path: \teeworlds.exe</code>
 *      <code>Root game-folder: Teeworlds</code>
 *
 * @see GameInfoHelper
 */
public class Game {

    private String name,
                   version,
                   connectParam,
                   exeFileRelative;
    private boolean connectDirect;

    /**
     * Constructs a new {@link Game} object. Initialize all {@link Game} variables with the values from the specified
     * {@link Properties} file.
     *
     * @param properties {@link Properties} file from a game.
     *
     * @see Game
     */
    public Game(Properties properties){
        this.name = properties.getProperty("name");
        this.exeFileRelative = properties.getProperty("exe.file");
        this.version = GameInfoHelper.getVersion(properties);
        if(Boolean.valueOf(properties.getProperty("connect.direct"))){
            connectDirect = true;
            connectParam = properties.getProperty("connect.param");
        }else{
            connectDirect = false;
            connectParam = "";
        }
    }
    /**
     * @return Proper name of the Game.
     */
    public String getName() {
        return name;
    }
    /**
     * @return version of the Game.
     */
    public String getVersion() {
        return version;
    }
    /**
     * @return .exe-file relative path to the root game-folder.
     *
     * @see Game
     */
    public String getExeFileRelative() {
        return exeFileRelative;
    }
    /**
     * If <code>true</code> the connection-parameter can be accessed via
     * {@link #getConnectParam()}.
     *
     * @return <code>true</code> if the game can connect direct to a server over ip, else <code>false</code>.
     */
    public boolean isConnectDirect(){
        return connectDirect;
    }
    /**
     * Returning String contains <code>?</code> as a placeholder for the IP-address of the server. Full
     * connection-parameter with insert IP-address can be obtained by calling {@link #getConnectParam(String)}.
     *
     * @return the connection-parameter with placeholder for the IP-address for this Game, if {@link #isConnectDirect()}
     * is <code>true</code>. Else an empty {@link String} is returned.
     */
    public String getConnectParam(){
        return connectParam;
    }
    /**
     * Return {@link #getConnectParam()} with the placeholder replaced with the IP-address of the server connecting to.
     *
     * @param ip IP-address of the server connecting to.
     *
     * @return the connection-parameter for this Game, if {@link #isConnectDirect()} is <code>true</code>. Else an empty
     * {@link String} is returned.
     */
    public String getConnectParam(String ip){
        if(!isConnectDirect()) return getConnectParam();
        String cParam = getConnectParam();
        return cParam.replace("?", ip);
    }
}
