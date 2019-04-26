package client;

import client.download.ImageDownload;
import client.download.GameDownload;
import client.download.GameDownloadManager;
import client.filedrop.FileDropClient;
import client.filedrop.FileDropServer;
import client.monitor.GameMonitor;
import client.monitor.GameProcess;
import client.monitor.Monitor;
import client.monitor.ServerMonitor;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.sun.istack.internal.Nullable;
import entities.game.Game;
import entities.game.GameList;
import entities.game.GameStatus;
import entities.server.ServerStatus;
import entities.settings.ClientSettings;
import entities.user.User;
import entities.user.UserList;
import entities.user.UserRunGamesList;
import entities.user.UserRunServerList;
import helper.GameFolderHelper;
import helper.kryo.NetworkClassRegistrationHelper;
import message.*;
import org.apache.log4j.Logger;
import requests.ImageDownloadRequest;
import requests.DownloadRequest;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * {@link LANClient} manages all communication with the <b>LANServer</b>. Also it handles the communication as an
 * interface to the GUI and implements all needed back-end functionality.
 * @author Levin Jeck
 * @version 2.0
 * @since 1.0
 */
public class LANClient extends Client {
    //Logging
    private static Logger log = Logger.getLogger(LANClient.class);
    //Buffersizes
    private static int writeBufferSize = 1048576,
                       objectBufferSize = 1048576;

    private FileDropServer fileDropServer;
    private GameDownloadManager gameDownloadManager;
    private ClientSettings settings;
    private volatile ServerStatus serverStatus;
    private volatile User user;
    private volatile UserList users;
    private volatile GameList games;
    private volatile Monitor gamemonitor;
    private volatile Monitor servermonitor;
    private volatile UserRunGamesList rungameslist;
    private volatile UserRunServerList runserverlist;


    /**
     * Creates the {@link LANClient} with a writebuffersize of {@value writeBufferSize} and a objectbuffersize of
     * {@value objectBufferSize}. All listeners are registered. Loading the {@link ClientSettings}, starts the
     * {@link LANClient} and starts searching for an open <b>LANServer</b>.
     */
    public LANClient(){
        super(writeBufferSize, objectBufferSize);
        gameDownloadManager = new GameDownloadManager();
        gamemonitor = new GameMonitor(this);
        servermonitor = new ServerMonitor(this);
        serverStatus = new ServerStatus();
        users = new UserList();
        games = new GameList();
        //Register listener and classes to be send over KryoNet
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();
        //Load client settings & user informations
        try {
            settings = new ClientSettings(true);
            user = new User(settings);
        } catch (Exception e) {
            log.fatal("User creation was not possible.");
            log.debug("User creation was not possible. CLIENTSETTINGS: " + settings + ", USER: " + user, e);
            System.exit(-1);
        }
        //Starts the server for file dropping function
        fileDropServer = new FileDropServer(user.getGamepath());
        //Starts the client
        new Thread(this).start();
        log.info("LANClient started.");
        connect();
    }

    /**
     * Tries to connect the {@link LANClient} with the <b>LANServer</b> if no connection is active.
     */
    private void connect(){
        new Thread(() -> {
            //Amount of while loop passings
            int reconnects = 0;
            while(!isConnected()){
                //Receiving server ip-address
                InetAddress serveraddress = discoverHost(settings.getServerUdp(), 5000);
                //Connect to server if any is running
                if(serveraddress != null) {
                    try {
                        connect(500, serveraddress, settings.getServerTcp(), settings.getServerUdp());
                    } catch (IOException e) {
                        log.error("Can not connect to server.");
                        log.debug("Can not connect to server. IP-ADDRESS: " + serveraddress.getHostAddress(), e);
                    }
                } else {
                    reconnects++;
                    log.info("No LANServer is running for " + reconnects * 5 + " seconds. Wait for the server to go " +
                            "online.");
                }
            }
        }).start();
    }

    /**
     * Registers all listeners.
     */
    private void registerListener(){
        registerLoginListener();
        registerDisconnectListener();
        registerGamelistListener();
        registerUserlistListener();
        registerErrorListener();
        registerUserRunGamesListener();
        registerUserRunServersListener();
        registerDownloadStopListener();
    }

    /**
     * Register listener for the server login. Sending {@link UserRunGamesList}, {@link UserRunServerList},
     * {@link ImageDownloadRequest} and sets the <b>LANServer</b> ip-address to the {@link ServerStatus} and calls
     * {@link ServerStatus#connected()}.
     */
    private void registerLoginListener(){
        addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                sendTCP(new LoginMessage(user));
                sendTCP(new UserRunGameMessage(user, gamemonitor.getRunningProcesses()));
                sendTCP(new UserRunServerMessage(user, servermonitor.getRunningProcesses()));
                ImageDownload imagedownload = new ImageDownload(user);
                sendTCP(new ImageDownloadRequest(user, imagedownload.getPort()));
                serverStatus.setServerIP(connection.getRemoteAddressTCP().getAddress().getHostAddress());
                serverStatus.connected();
                log.info("Successfully logged into the LANServer.");
                log.debug("Successfully logged into the LANServer. IP-ADDRESS: " + serverStatus.getServerIP());
            }
        });
    }

    /**
     * Register listener for the server disconnect. Calls {@link ServerStatus#disconnected()} and {@link #connect()}.
     */
    private void registerDisconnectListener(){
        addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                serverStatus.disconnected();
                log.info("Lost the connection to the LANServer.");
                log.debug("Lost the connection to the LANServer. IP-ADDRESS: " + serverStatus.getServerIP());
                serverStatus.setServerIP("");
                connect();
            }
        });
    }

    /**
     * Register listener if a {@link GamelistMessage} is received.
     */
    private void registerGamelistListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof GamelistMessage){
                    GamelistMessage message = (GamelistMessage)object;
                    games = message.games;
                    log.info("Received a gamelist from the LANServer.");
                    log.debug("Received a gamelist from the LANServer. IP-ADDRESS: " + serverStatus.getServerIP());
                }
            }
        });
    }

    /**
     * Register listener if a {@link UserlistMessage} is received.
     */
    private void registerUserlistListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserlistMessage){
                    UserlistMessage message = (UserlistMessage)object;
                    users = message.users;
                    log.info("Received a userlist from the LANServer.");
                    log.debug("Received a userlist from the LANServer. IP-ADDRESS: " + serverStatus.getServerIP());
                }
            }
        });
    }

    /**
     * Register listener if a {@link ErrorMessage} is received.
     */
    private void registerErrorListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof ErrorMessage){
                    ErrorMessage message = (ErrorMessage)object;
                    log.error("Error from the LANServer: " + message.error);
                }
            }
        });
    }

    /**
     * Register listener if a {@link UserRunGamesList} is received.
     */
    private void registerUserRunGamesListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserRunGamesList){
                    rungameslist = (UserRunGamesList)object;
                    log.info("Received a userrungameslist from the LANServer.");
                    log.debug("Received a userrungameslist from the LANServer. IP-ADDRESS: " + serverStatus.getServerIP());
                }
            }
        });
    }

    /**
     * Register listener if a {@link UserRunServerList} is received.
     */
    private void registerUserRunServersListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserRunServerList){
                    runserverlist = (UserRunServerList)object;
                    log.info("Received a userrunserverlist from the LANServer.");
                    log.debug("Received a userrunserverlist from the LANServer. IP-ADDRESS: " + serverStatus.getServerIP());
                }
            }
        });
    }

    /**
     * Registers listener if a {@link DownloadStopMessage} is received.
     */
    private void registerDownloadStopListener(){
        addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof DownloadStopMessage){
                    DownloadStopMessage message = (DownloadStopMessage)object;
                    //If all gamedownloads should be stopped, User and Game are null
                    if(message.user == null && message.game == null){
                        gameDownloadManager.stopAll();
                        log.info("LANServer stopped all active downloads.");
                        log.debug("LANServer stopped all active downloads. IP-ADDRESS: " + serverStatus.getServerIP() +
                                "- USER: " + message.user + "- GAME: " + message.game);
                    }
                    //Else User is null and Game is the game to stop downloading
                    else if(message.user == null){
                        gameDownloadManager.stop(message.game);
                        log.info("LANServer stopped the active download of '" + message.game + "'.");
                        log.debug("LANServer stopped the active download of '" + message.game + "'. IP-ADDRESS: "
                                + serverStatus.getServerIP() + "- USER: " + message.user);
                    }
                }
            }
        });
    }

    /**
     * @return {@link ServerStatus} of the connected <b>LANServer</b>.
     */
    public ServerStatus getStatus(){
        return serverStatus;
    }

    /**
     * @return {@link User} of the {@link LANClient}.
     */
    public User getUser(){
        return user;
    }

    /**
     * @return {@link UserList} of the <b>LANServer</b>.
     */
    public UserList getUserList(){
        return users;
    }

    /**
     * @return {@link GameList} of the <b>LANServer</b>.
     */
    public GameList getGames(){
        return games;
    }

    /**
     * Generates the {@link GameStatus} of a game. The local availability, the playability, the updatestatus, the running,
     * the downloading/unzipping and progress fields are set.
     *
     * @param game the game of which the {@link GameStatus} will be generated
     * @return {@link GameStatus} of the game
     */
    public GameStatus getGameStatus(Game game){
        GameStatus gamestatus = new GameStatus();
        //Get uptodate state of the game
        int uptodate = game.isUptodate();
        switch(uptodate){
            case -1: gamestatus.setLocal(false);
                     break;
            case -2: gamestatus.setPlayable(true);
                     gamestatus.setVersion(false);
                     gamestatus.setLocal(true);
                     break;
            case -3: gamestatus.setUpdate(true);
                     gamestatus.setLocal(true);
                     break;
            case 0: gamestatus.setPlayable(true);
                    gamestatus.setLocal(true);
        }
        //Check if game is running or not
        gamestatus.setRunning(gamemonitor.getRunningProcesses().contains(game));
        //Check if game is downloaded, else send games status
        if(!gameDownloadManager.isDownloading(game))
            return gamestatus;
        //Set download/unzip progress if downloading/unzipping
        GameDownload gameDownload = gameDownloadManager.getDownload(game);
        if(gameDownload.getDownloadprogress() < 1.){
            gamestatus.setDownloading(true);
            gamestatus.setDownloadProgress(gameDownload.getDownloadprogress());
            gamestatus.setDownloadSpeed(gameDownload.getAverageDownloadspeed()/1048576 + " MB/sec");
        }else{
            gamestatus.setUnzipping(true);
            gamestatus.setUnzipProgress(gameDownload.getUnzipprogress());
        }
        return gamestatus;
    }

    /**
     * Updates the {@link User} locally and syncs it with the <b>LANServer</b>. On every call an
     * {@link ImageDownloadRequest} is made.
     *
     * @param user new {@link User} object, which is used to copy all information to the current {@link User}
     * @return <b>true</b> if any information changed and has been successfully updated and saved, else <b>false</b>
     */
    public boolean updateUser(User user) {
        try {
            //Update user information
            if(this.user.update(user)) {
                //Send new user to the server
                ImageDownload imagedownload = new ImageDownload(user);
                sendTCP(new ImageDownloadRequest(user, imagedownload.getPort()));
                sendTCP(new UserupdateMessage(user));
                return true;
            }
        } catch (IOException e) {
            log.warn("Could not updated user information.", e);
        }
        return false;
    }

    /**
     * Downloading the {@link Game} from the <b>LANServer</b>, if it is not downloading already and if there is enough
     * free space on the disk.
     *
     * @param game {@link Game} to download
     * @return <b>-1</b> if the {@link Game} is downloading already, <b>-2</b> if the {@link LANClient} is not connected
     * to the <b>LANServer</b>, <b>-3</b> if there is not enough free space on the disk, <b>0</b> if the download
     * started successfully.
     */
    public int download(Game game){
        //Check if the game is downloading already
        if(gameDownloadManager.isDownloading(game))
            return -1;
        //Check if the client has a connection to the server
        if(!serverStatus.isConnected())
            return -2;
        //Download game
        GameDownload download = new GameDownload(game, user);
        gameDownloadManager.add(download);
        //Send download request
        sendTCP(new DownloadRequest(game, download.getPort()));
        log.info("Requested download of the game '" + game + "'.");
        //Get the gamesize
        long gamesize = download.getFileSizeAndConnect();
        //Check for enough free space on the disk
        File space = new File(user.getGamepath());
        if(gameDownloadManager.getSizeRemaining() > space.getFreeSpace()){
            log.error("Not enough free space to download '" + game + "' with size of "
                    + (double)Math.round((double)gamesize/10485.76)/100. + " MByte.");
            gameDownloadManager.remove(download);
            return -3;
        }
        //Start the download and extraction of the gamefile
        download.start();
        return 0;
    }

    /**
     * Starts the {@link Game} with the working directory of the {@code folderpath}. To pass any command-line arguments
     * {@code parameters} can be used.
     *
     * @param game {@link Game} to start a server or the {@code Game} itself
     * @param folderpath path to the {@code game} folder
     *                   TODO
     * @param exepath relative path within the game folder of the exe.
     * @param parameters command-line arguments that should be passed to the game starting.
     * @return the process, which represents the started game.
     * @throws IOException if an error occurs while starting the game
     */
    private Process startProcess(Game game, String folderpath, String exepath, String... parameters) throws IOException {
        //Build command list for ProcessBuilder
        List<String> commands = new ArrayList<>();
        commands.add(folderpath + exepath);
        commands.addAll(parseParameter(parameters));
        //Set up ProcessBuilder
        ProcessBuilder process = new ProcessBuilder(commands).inheritIO();
        process.directory(new File(GameFolderHelper.getGameFolder(game.getExeFileRelative())));
        //Start process
        return process.start();
    }

    /**
     * Parses the parameters passed to {@link #startProcess(Game, String, String, String...)} to solve server start
     * bug of any game with server parameters starting without '-' or '+'.
     *
     * @param args parameters to be parsed.
     * @return parsed paramters.
     */
    private List<String> parseParameter(String... args){
        List<String> arguments = new ArrayList<>();
        for(String arg : args){
            List<Integer> pos = new ArrayList<>();

            if(arg.trim().isEmpty())
                continue;

            //Space added cause of 'Call of Duty 4' bug can't finding the maps.
            if(arg.startsWith("+") ||arg.startsWith("-")) {
                arguments.add(arg + " ");
                continue;
            }

            int first = arg.indexOf(" ");
            arguments.add(arg.substring(0, first).trim());
            arguments.add(arg.substring(first, arg.length() - 1).trim());

        }
        return arguments;
    }

    /**
     * Starts the {@link Game} if it is locally available and up-to-date, else {@link #download(Game)} is called first.
     * After the {@link Game} is started it is passed to the {@link GameMonitor}.
     *
     * @param game {@link Game} to start
     * @return <b>true</b> if the {@link Game} has been successfully started, else <b>false</b>
     */
    public boolean startGame(Game game, boolean download){
        //Check if the game is up-to-date/locally available.
        int uptodate = game.isUptodate();
        if(uptodate != 0 && uptodate != -2 && download){
            switch(uptodate){
                case -1: log.info("'" + game + "' can not be found in the gamepath '" + user.getGamepath() + "'.");
                case -3: log.info("'" + game + "' is not up-to-date.");
            }
            //Update the game
            download(game);
            //Wait until the game is downloaded and start it
            new Thread(() -> {
                while(gameDownloadManager.isDownloading(game)){
                    try { sleep(10); } catch (InterruptedException e) { }
                }
                startGame(game, false);
            }).start();
            return false;
        }
        //Start game and add it to the gamemonitor
        Process gameprocess;
        try {
            gameprocess = startProcess(game, GameFolderHelper.getGameFolder(game.getExeFileRelative()),
                                                game.getExeFileRelative(), game.getParam());
            gamemonitor.add(new GameProcess(game, gameprocess));
        } catch (Exception e) {
            log.error("Can not launch the game '" + game + "'.", e);
            return false;
        }
        return true;
    }



    /**
     * Sends the running games to the LANServer.
     */
    public void updateOpenGames(){
        sendTCP(new UserRunGameMessage(user, gamemonitor.getRunningProcesses()));
    }

    /**
     * Sends the running servers to the LANServer.
     */
    public void updateOpenServers(){
        sendTCP(new UserRunServerMessage(user, servermonitor.getRunningProcesses()));
    }

    /**
     * Opens the exe file from the game at the explorer.
     *
     * @param game game that should be opened in the explorer.
     * @return true if the explorer could be opened, else false.
     */
    public boolean openExplorer(Game game){
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + GameFolderHelper.getAbsolutePath(game.getExeFileRelative()));
        } catch (IOException e) {
            log.error("Can not open 'explorer.exe'.", e);
            return false;
        }
        return true;
    }

    /**
     * Starts a game and connects it direct to the server with the given ip address, if the game is capable of direct
     * connect through console-line arguments.
     *
     * @param game game that should be started and connected to the server with the given ip.
     * @param ip ip address of the server the game should connect to.
     * @return true if the game STARTED properly, else false.
     */
    public boolean connectServer(Game game, String ip, boolean download){
        //Check if the game is up-to-date/locally available.
        if(!game.isConnectDirect())
            return false;
        int uptodate = game.isUptodate();
        if(uptodate != 0 && uptodate != -2 && download){
            switch(uptodate){
                case -1: log.info("'" + game + "' can not be found in the gamepath '" + user.getGamepath() + "'.");
                case -3: log.info("'" + game + "' is not up-to-date.");
            }
            //Update the game
            download(game);
            //Wait until the game is downloaded and start it
            new Thread(() -> {
                while(gameDownloadManager.isDownloading(game)){
                    try { sleep(10); } catch (InterruptedException e) { }
                }
                connectServer(game, ip, false);
            }).start();
            return false;
        }
        //Start game and add it to the gamemonitor
        Process gameprocess;
        try {
            String connectparameter = game.getConnectParam().replace("?", ip);
            gameprocess = startProcess(game, GameFolderHelper.getGameFolder(game.getExeFileRelative()),
                    game.getExeFileRelative(), game.getParam(), connectparameter);
            gamemonitor.add(new GameProcess(game, gameprocess));
        } catch (IOException e) {
            log.error("Can not connect the game '" + game + "' to '" + ip + "'.", e);
            return false;
        }
        return true;
    }

    /**
     * Starts the server for a game. If possible a dedicated server is created, else an in-game server is started.
     * The server gets started with the parameters provided from the user, who creates it.
     *
     * @param game game to start a server from.
     * @param param server command-line arguments.
     * @return true if the server STARTED properly, else false.
     */
    public boolean startServer(Game game, String param, boolean download){
        if(!game.isOpenServer())
            return false;
        int uptodate = game.isUptodate();
        if(uptodate != 0 && uptodate != -2 && download){
            switch(uptodate){
                case -1: log.info("'" + game + "' can not be found in the gamepath '" + user.getGamepath() + "'.");
                case -3: log.info("'" + game + "' is not up-to-date.");
            }
            //Update the game
            download(game);
            //Wait until the game is downloaded and start it
            new Thread(() -> {
                while(gameDownloadManager.isDownloading(game)){
                    try { sleep(10); } catch (InterruptedException e) { }
                }
                startServer(game, param, false);
            }).start();
            return false;
        }
        //Start game and add it to the gamemonitor
        Process gameprocess;
        try {
            gameprocess = startProcess(game, GameFolderHelper.getGameFolder(game.getExeFileRelative()),
                    game.getExeServerRelative(), param);
            servermonitor.add(new GameProcess(game, gameprocess));
        } catch (IOException e) {
            log.error("Can not start the server of the game '" + game + "'.", e);
            return false;
        }
        return true;
    }

    /**
     * Stops the download or extraction of the specified game and notifies the LANServer.
     *
     * @param game game to stop downloading or extracting.
     * @return true
     */
    public boolean stopDownloadUnzip(Game game){
        gameDownloadManager.getDownload(game).stopDownloadUnzip();
        sendTCP(new DownloadStopMessage(user, game));
        return true;
    }

    /**
     * @return userrungameslist with all running games of the users.
     */
    public UserRunGamesList getUserRunGames(){
        return rungameslist;
    }

    /**
     * @return userrunserverlist with all running servers of the users.
     */
    public UserRunServerList getUserRunServer(){
        return runserverlist;
    }

    /**
     * Kills a game subprocess.
     *
     * @param game game to stop.
     * @return true if the process was killed, else false.
     */
    public boolean stopGame(Game game){
        return gamemonitor.stop(game);
    }





    //TODO

    public boolean sendFiles(User user, List<File> files){
        new FileDropClient(user, files);
        return false;
    }

    public boolean getDropFileDownloadStatus(){
        return fileDropServer.isDownloading();
    }

}
