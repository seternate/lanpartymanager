package client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.*;
import helper.GameFolderHelper;
import helper.NetworkClassRegistrationHelper;
import message.*;
import requests.DownloadRequest;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.Objects;

import static java.lang.Thread.sleep;

public final class MyClient extends com.esotericsoftware.kryonet.Client {
    private UserList users;
    private GameList games;
    private DownloadManager downloadManager;
    private User user;
    private ClientSettings settings;
    private ServerStatus serverStatus;
    private DragAndDropServer dndserver;


    public MyClient(){
        super(1048576, 1048576);
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();

        try {
            settings = new ClientSettings(true, true);
            user = new User(settings);
        } catch (IOException e) {
            System.err.println("ERROR: User creation was not possible.");
            System.exit(-10);
        }
        downloadManager = new DownloadManager();
        serverStatus = new ServerStatus();
    }

    @Override
    public void start(){
        super.start();
        new Thread(this).start();
        connect();
        dndserver = new DragAndDropServer(user.getGamepath());
    }

    private void connect(){
        new Thread(() -> {
            while(!isConnected()){
                InetAddress address = discoverHost(settings.getServerUdp(), 5000);
                if(address != null) {
                    try {
                        connect(500, address, settings.getServerTcp(), settings.getServerUdp());
                    } catch (IOException e) {
                        System.err.println("ERROR: Cannot connect to server '" + address.getHostAddress() + "'.");
                    }
                } else
                    System.err.println("ERROR: No lan-server running.");
            }
        }).start();
    }

    private void registerListener(){
        addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                sendTCP(new LoginMessage(user));
                System.out.println("LOGIN: Logged into lan-server.");
                serverStatus.setServerIP(connection.getRemoteAddressTCP().getAddress().getHostAddress());
                serverStatus.connected();
            }
            @Override
            public void disconnected(Connection connection) {
                serverStatus.disconnected();
                connect();
                System.err.println("ERROR: Lan-server connection lost.");
            }
            @Override
            public void received (Connection connection, Object object) {
                if(object instanceof GamelistMessage){
                    GamelistMessage message = (GamelistMessage)object;
                    games = message.games;
                    System.out.println("RECEIVED: Games");
                }
                if(object instanceof UserlistMessage){
                    UserlistMessage message = (UserlistMessage)object;
                    users = message.users;
                    System.out.println("RECEIVED: Users");
                }
                if(object instanceof ErrorMessage){
                    ErrorMessage message = (ErrorMessage)object;
                    System.err.println("ERROR: " + message.error);
                }
            }
        });
    }

    public ServerStatus getStatus(){
        return serverStatus;
    }

    public GameList getGames(){
        return games;
    }

    public GameStatus getGameStatus(Game game){
        GameStatus gamestatus = new GameStatus();
        int uptodate = game.isUptodate();
        switch(uptodate){
            case -1: gamestatus.download = true; break;
            case -2: gamestatus.playable = true; gamestatus.version = false; break;
            case -3: gamestatus.update = true; break;
            case 0: gamestatus.playable = true;
        }
        Download download = downloadManager.getDownloadStatus(game);
        if(download == null)
            return gamestatus;
        if(download.receivedParts < download.totalParts){
            gamestatus.downloading = true;
            gamestatus.downloadProgress = download.downloadProgress;
        }else{
            gamestatus.unzipping = true;
            gamestatus.unzipProgress = download.unzipProgress;
        }
        return gamestatus;
    }

    public User getUser(){
        return user;
    }

    public UserList getUserList(){
        return users;
    }

    public boolean updateUser(User user) {
        try {
            if(this.user.update(user)) {
                sendTCP(new UserupdateMessage(user));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean startGame(Game game){
        if(game.isUptodate() != 0 && game.isUptodate() != -2){
            download(game);
            new Thread(() -> {
                while(downloadManager.getDownloadStatus(game) != null){
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
        if(downloadManager.getDownloadStatus(game) != null){
            return -1;
        }
        File sFile = new File(user.getGamepath());
        if(game.getSizeServer() > sFile.getFreeSpace())
            return -2;
        int openport = getOpenPort();
        downloadManager.add(new Download(openport, game, game.getSizeServer(), user.getGamepath()));
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

    public boolean startServer(Game game, String param){
        if(!game.isOpenServer())
            return false;
        if(game.isUptodate() != 0 && game.isUptodate() != -2) {
            download(game);
            new Thread(() -> {
                while(downloadManager.getDownloadStatus(game) != null){
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

    public boolean connectServer(Game game, String ip){
        if(!game.isConnectDirect())
            return false;
        if(game.isUptodate() != 0 && game.isUptodate() != -2) {
            download(game);
            new Thread(() -> {
                while(downloadManager.getDownloadStatus(game) != null){
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

    private int getOpenPort(){
        ServerSocket server = null;
        try {
            server = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int freeport = Objects.requireNonNull(server).getLocalPort();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freeport;
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
        new DragAndDropClient(user, files);
        return false;
    }

    public boolean getDropFileDownloadStatus(){
        return dndserver.isDownloading();
    }

}
