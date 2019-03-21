package client;

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
import entities.game.Game;
import entities.game.GameList;
import entities.game.GameStatus;
import entities.server.ServerStatus;
import entities.settings.ClientSettings;
import entities.user.User;
import entities.user.UserList;
import helper.GameFolderHelper;
import helper.NetworkHelper;
import helper.kryo.NetworkClassRegistrationHelper;
import message.*;
import org.apache.log4j.Logger;
import requests.DownloadRequest;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Creates the LANClient required for lanpartymanager usage.
 */
public class LANClient extends Client {
    private static Logger log = Logger.getLogger(LANClient.class);

    private FileDropServer fileDropServer;
    private GameDownloadManager gameDownloadManager;
    private volatile ServerStatus serverStatus;
    private ClientSettings settings;
    private volatile User user;
    private volatile UserList users;
    private volatile GameList games;
    private volatile Monitor gamemonitor;
    private volatile Monitor servermonitor;


    /**
     * Creates and setup the client. Registering all needed listeners and classes for KryoNet. Loading the client
     * settings, specified the object & write buffer, creates the user and starts the client.
     */
    public LANClient(){
        super(1048576, 1048576);
        gameDownloadManager = new GameDownloadManager();
        gamemonitor = new GameMonitor(this);
        servermonitor = new ServerMonitor(this);
        serverStatus = new ServerStatus();
        //Register listener and classes to be send over KryoNet
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();

        //Load client settings & user informations
        try {
            settings = new ClientSettings(true);
            user = new User(settings);
        } catch (Exception e) {
            log.fatal("User creation was not possible.", e);
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
     * Connects the client to the LANServer if it is not connected.
     */
    private void connect(){
        new Thread(() -> {
            int reconnects = 0;
            while(!isConnected()){
                //Receiving server ip-address
                InetAddress serveraddress = discoverHost(settings.getServerUdp(), 5000);
                //Connect to server if any is running
                if(serveraddress != null) {
                    try {
                        connect(500, serveraddress, settings.getServerTcp(), settings.getServerUdp());
                    } catch (IOException e) {
                        log.error("Connection to server with ip-address '" + serveraddress.getHostAddress()
                                + "' is not possible.", e);
                    }
                } else {
                    reconnects++;
                    log.warn("No LANServer is running for " + reconnects * 5 + " seconds. Wait for the server to go " +
                            "online.");
                }
            }
        }).start();
    }

    /**
     * Registers all needed listeners.
     */
    private void registerListener(){
        registerLoginListener();
        registerDisconnectListener();
        registerGamelistListener();
        registerUserlistListener();
        registerErrorListener();
    }

    /**
     * Register listener for login.
     */
    private void registerLoginListener(){
        addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                sendTCP(new LoginMessage(user));
                serverStatus.setServerIP(connection.getRemoteAddressTCP().getAddress().getHostAddress());
                serverStatus.connected();
                log.info("Successfully logged into the LANServer '" + serverStatus.getServerIP() + "'.");
            }
        });
    }

    /**
     * Register listener for disconnect.
     */
    private void registerDisconnectListener(){
        addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                serverStatus.disconnected();
                log.info("Lost the connection to the LANServer.");
                connect();
            }
        });
    }

    /**
     * Register listener for gamelists.
     */
    private void registerGamelistListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof GamelistMessage){
                    GamelistMessage message = (GamelistMessage)object;
                    games = message.games;
                    log.info("Received gamelist from the server '" + serverStatus.getServerIP() + "'.");
                }
            }
        });
    }

    /**
     * Register listener for userlists.
     */
    private void registerUserlistListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UserlistMessage){
                    UserlistMessage message = (UserlistMessage)object;
                    users = message.users;
                    log.info("Received userlist from the server '" + serverStatus.getServerIP() + "'.");
                }
            }
        });
    }

    /**
     * Register listener for errors.
     */
    private void registerErrorListener(){
        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof ErrorMessage){
                    ErrorMessage message = (ErrorMessage)object;
                    log.info("message.error");
                }
            }
        });
    }

    /**
     * @return status of the server.
     */
    public ServerStatus getStatus(){
        return serverStatus;
    }

    /**
     * @return user of this client.
     */
    public User getUser(){
        return user;
    }

    /**
     * @return userlist from the server.
     */
    public UserList getUserList(){
        return users;
    }

    /**
     * @return gamelist from the server.
     */
    public GameList getGames(){
        return games;
    }

    /**
     * Generates the status of the specified game. Checks if its locally available, playable, version information
     * can be determined, has to be updated and its download & unzip progress.
     *
     * @param game the status from this game will be generated
     * @return status of the specified game
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
        //Check if game is downloaded, else send games status
        if(!gameDownloadManager.isDownloading(game))
            return gamestatus;
        //Set download/unzip progress if downloading/unzipping
        GameDownload gameDownload = gameDownloadManager.getDownload(game);
        if(gameDownload.getDownloadprogress() < 1.){
            gamestatus.setDownloading(true);
            gamestatus.setDownloadProgress(gameDownload.getDownloadprogress());
        }else{
            gamestatus.setUnzipping(true);
            gamestatus.setUnzipProgress(gameDownload.getUnzipprogress());
        }
        return gamestatus;
    }

    /**
     * Updates the user information and send it to the server.
     *
     * @param user user with new information, that should be changed
     * @return true if any changes detected and successfully saved, false anything goes wrong.
     */
    public boolean updateUser(User user) {
        try {
            //Update user information
            if(this.user.update(user)) {
                //Send new user to the server
                sendTCP(new UserupdateMessage(user));
                return true;
            }
        } catch (IOException e) {
            log.warn("Could not updated user information.", e);
        }
        return false;
    }

    /**
     * Downloads a game if it is not downloading already and there is enough free space on the disk where the gamepath
     * leeds to.
     *
     * @param game game that should be downloaded.
     * @return -1 if the game is downloading already, -2 if the client is not connected to the server, 0 if the
     * download started successfully.
     */
    public int download(Game game){
        //Check if the game is downloading already
        if(gameDownloadManager.isDownloading(game))
            return -1;
        //Check if the client has a connection to the server
        if(!serverStatus.isConnected())
            return -2;
        //TODO: Feedback for no free space on disk.
        //Download game
        GameDownload download = new GameDownload(game, user);
        gameDownloadManager.add(download);
        log.info("Requested download of the game '" + game + "'.");
        sendTCP(new DownloadRequest(game, download.getPort()));
        return 0;
    }

    /**
     * Starts a game if it is locally available and up-to-date, else it is downloaded first. After the game is started
     * it is passed to the GameMonitor to keep track of the game process status.
     *
     * @param game game that should be started.
     * @return true if the game has been successfully started, else it returns false.
     */
    public boolean startGame(Game game){
        //Check if the game is up-to-date/locally available.
        int uptodate = game.isUptodate();
        if(uptodate != 0 && uptodate != -2){
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
                startGame(game);
            }).start();
            return false;
        }
        //Start game and add it to the gamemonitor
        Process gameprocess;
        try {
            gameprocess = startProcess(game, GameFolderHelper.getGameFolder(game.getExeFileRelative()),
                                                game.getExeFileRelative(), game.getParam());
            gamemonitor.add(new GameProcess(game, gameprocess));
        } catch (IOException e) {
            log.error("Can not launch the game '" + game + "'.", e);
            return false;
        }
        return true;
    }

    /**
     * Starts the specified game within the working directory of the folderpath with the exepath. Command-Line arguments
     * for the started game can be passed through parameters.
     *
     * @param game which should the game or a server be started.
     * @param folderpath path of the root game folder.
     * @param exepath relative path within the game folder of the exe.
     * @param parameters command-line arguments that should be passed to the game starting.
     * @return the process, which represents the started game.
     * @throws IOException if an error occurs while starting the game
     */
    private Process startProcess(Game game, String folderpath, String exepath, String... parameters) throws IOException {
        //Build command list for ProcessBuilder
        List<String> commands = new ArrayList<>();
        commands.add(folderpath + exepath);
        commands.addAll(Arrays.asList(parameters));
        //Set up ProcessBuilder
        ProcessBuilder process = new ProcessBuilder(commands);
        process.directory(new File(GameFolderHelper.getGameFolder(game.getExeFileRelative())));
        process.redirectErrorStream(true);
        //Start process
        return process.start();
    }

    public void updateOpenGames(){
        //TODO
    }

    public void updateOpenServers(){
        //TODO
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

    public boolean connectServer(Game game, String ip){
        if(!game.isConnectDirect())
            return false;
        if(game.isUptodate() != 0 && game.isUptodate() != -2) {
            download(game);
            new Thread(() -> {
                while(gameDownloadManager.isDownloading(game)){
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                connectServer(game, ip);
            }).start();
            return false;
        }
        String start;
        if(game.getParam().equals(""))
            start = "start " + game.getExeFileRelative().substring(1);
        else
            start = "start " + game.getExeFileRelative().substring(1) + " " + game.getParam();
        String parameterserver = game.getConnectParam().replace("?", ip);
        return false;//startProcess(game, start + " " + parameterserver);
    }

    public boolean startServer(Game game, String param){
        if(!game.isOpenServer())
            return false;
        if(game.isUptodate() != 0 && game.isUptodate() != -2) {
            download(game);
            new Thread(() -> {
                while(gameDownloadManager.isDownloading(game)){
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                startServer(game, param);
            }).start();
            return false;
        }
        String start;
        if(param.equals(""))
            start = "start " + "\"\" " + "\"" + game.getExeServerRelative().substring(1) + "\"";
        else
            start = "start " + "\"\" " + "\"" + game.getExeServerRelative().substring(1) + "\"" + " " + param;
        return false;//startProcess(game, start);
    }



    public boolean sendFiles(User user, List<File> files){
        new FileDropClient(user, files);
        return false;
    }

    public boolean getDropFileDownloadStatus(){
        return fileDropServer.isDownloading();
    }

}
