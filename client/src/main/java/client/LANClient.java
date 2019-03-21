package client;

import client.FileDrop.FileDropClient;
import client.FileDrop.FileDropServer;
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


    /**
     * Creates and setup the client. Registering all needed listeners and classes for KryoNet. Loading the client
     * settings, specified the object & write buffer, creates the user and starts the client.
     */
    public LANClient(){
        super(1048576, 1048576);
        gameDownloadManager = new GameDownloadManager();
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
        GameDownload gameDownload = gameDownloadManager.getDownload(game);
        if(gameDownload == null) {
            return gamestatus;
        }
        //Set download/unzip progress if downloading/unzipping
        //TODO
        if(gameDownload.receivedParts < gameDownload.totalParts){
            gamestatus.setDownloading(true);
            gamestatus.setDownloadProgress(gameDownload.downloadProgress);
        }else{
            gamestatus.setUnzipping(true);
            gamestatus.setUnzipProgress(gameDownload.unzipProgress);
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











    public boolean startGame(Game game){
        if(game.isUptodate() != 0 && game.isUptodate() != -2){
            download(game);
            new Thread(() -> {
                while(gameDownloadManager.getDownload(game) != null){
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                startGame(game);
            }).start();
            return false;
        }
        String start;
        if(game.getParam().equals(""))
            start = "start " + "\"\" " + "\"" + game.getExeFileRelative().substring(1) + "\"";
        else
            start = "start " + "\"\" " + "\"" + game.getExeFileRelative().substring(1) + "\"" + " " + game.getParam();
        return startProcess(game, start);
    }

    public int download(Game game){
        if(gameDownloadManager.getDownload(game) != null){
            return -1;
        }
        File sFile = new File(user.getGamepath());
        if(game.getSizeServer() > sFile.getFreeSpace())
            return -2;
        int openport = NetworkHelper.getOpenPort();
        gameDownloadManager.add(new GameDownload(openport, game, game.getSizeServer(), user.getGamepath()));
        System.out.println("REQUEST: Download '" + game.getName() + "'.");
        sendTCP(new DownloadRequest(game, openport));
        return 0;
    }

    public boolean openExplorer(Game game){
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + GameFolderHelper.getAbsolutePath(game.getExeFileRelative()));
        } catch (IOException e) {
            e.printStackTrace();
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
                while(gameDownloadManager.getDownload(game) != null){
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
        return startProcess(game, start + " " + parameterserver);
    }

    public boolean startServer(Game game, String param){
        if(!game.isOpenServer())
            return false;
        if(game.isUptodate() != 0 && game.isUptodate() != -2) {
            download(game);
            new Thread(() -> {
                while(gameDownloadManager.getDownload(game) != null){
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
        return startProcess(game, start);
    }

    private boolean startProcess(Game game, String start) {
        try {
            ProcessBuilder process = new ProcessBuilder("cmd.exe", "/C", start);
            process.directory(new File(GameFolderHelper.getGameFolder(game.getExeFileRelative())));
            process.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendFiles(User user, List<File> files){
        new FileDropClient(user, files);
        return false;
    }

    public boolean getDropFileDownloadStatus(){
        return fileDropServer.isDownloading();
    }

}
