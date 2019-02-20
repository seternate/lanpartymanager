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
import java.util.Objects;

import static java.lang.Thread.sleep;

public final class MyClient extends com.esotericsoftware.kryonet.Client {
    private UserList users;
    private GameList games;
    private DownloadManager downloadManager;
    private User user;
    private ClientSettings settings;
    private ServerStatus serverStatus;


    public MyClient(){
        super();
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();

        try {
            settings = new ClientSettings(true, true);
            user = new User(settings);
        } catch (IOException e) {
            System.err.println("User creation was not possible.");
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
    }

    private void connect(){
        new Thread(() -> {
            while(!isConnected()){
                InetAddress address = discoverHost(settings.getServerUdp(), 5000);
                if(address != null) {
                    try {
                        connect(500, address, settings.getServerTcp(), settings.getServerUdp());
                    } catch (IOException e) {
                        System.err.println("No server running.");
                    }
                }
            }
        }).start();
    }

    private void registerListener(){
        addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                sendTCP(new LoginMessage(user));
                System.out.println("Logged in.");
                serverStatus.setServerIP(connection.getRemoteAddressTCP().getAddress().getHostAddress());
                System.out.println();
                serverStatus.connected();
            }
            @Override
            public void disconnected(Connection connection) {
                serverStatus.disconnected();
                connect();
                System.err.println("Server connection lost.");
            }
            @Override
            public void received (Connection connection, Object object) {
                if(object instanceof GamelistMessage){
                    GamelistMessage message = (GamelistMessage)object;
                    games = message.games;
                    System.out.println("Received games.");
                }
                if(object instanceof UserlistMessage){
                    UserlistMessage message = (UserlistMessage)object;
                    users = message.users;
                    System.out.println("Received users.");
                }
                if(object instanceof ErrorMessage){
                    ErrorMessage message = (ErrorMessage)object;
                    System.err.println(message.error);
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

    public boolean updateUser(User user) {
        try {
            return this.user.update(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
        System.out.println("Requested to download " + game.getName() + ".");
        sendTCP(new DownloadRequest(game, openport));
        return 0;
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
        String start = "start ";
        if(game.getParam().equals(""))
            start += game.getExeFileRelative().substring(1);
        else
            start += game.getExeFileRelative().substring(1) + " " + game.getParam();
        return startProcess(game, start);
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
        File file = new File(Objects.requireNonNull(GameFolderHelper.getAbsolutePath(game.getExeFileRelative())));
        try {
            ProcessBuilder process = new ProcessBuilder("cmd.exe", "/C", start);
            process.directory(file.getParentFile());
            process.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*


    public boolean updateUser(User user){
        boolean changed;
        changed = changeUsername(user.getUsername()) | changeGamepath(user.getGamepath());
        return changed;
    }

    public User getUser(){
        String winUser = System.getProperty("user.name");
        if(user.getGamepath().contains("C:\\Users\\")){
            String[] split = user.getGamepath().split("\\\\");
            if(split.length >= 3 && !split[2].equals(winUser)){
                StringBuilder path;
                path = new StringBuilder("C:\\Users\\" + winUser + "\\");
                for(int i = 3; i < split.length; i++){
                    path.append(split[i]).append("\\");
                }
                user.setGamepath(path.toString());
                PropertyHelper.setGamePath(path.toString());
            }
        }
        return user;
    }

    public List<Game> getNewGames(List<Game> games){
        if(this.games.size() != games.size())
            return this.games;
        for(Game game : games){
            boolean same = false;
            for(Game thisGame : this.games){
                if(game.equals(thisGame)){
                    same = true;
                    break;
                }
            }
            if(!same) return this.games;
        }
        return null;
    }

    public List<User> getNewUsers(List<User> users){
        if(this.users.size() != users.size()) {
            return new ArrayList<>(this.users.values());
        }
        for(User user : users){
            boolean same = false;
            for(User thisUser : this.users.values()){
                if(user.equals(thisUser)){
                    same = true;
                    break;
                }
            }
            if(!same) return new ArrayList<>(this.users.values());
        }
        return null;
    }

    public int download(Game game){
        if(game.isUptodate() == 0)
            return -1;
        File sFile = new File(PropertyHelper.getGamepath());
        if(game.getSizeServer() > sFile.getFreeSpace())
            return -2;
        int openport = getOpenPort();
        downloadManager.add(new Download(openport, game, game.getSizeServer()));
        System.out.println("Requested to download " + game.getName() + ".");
        sendTCP(new DownloadRequest(game, openport));
        return 0;
    }

    public GameStatus getGameStatus(Game game){
        GameStatus serverStatus = new GameStatus();
        int uptodate = game.isUptodate();
        switch(uptodate){
            case -1: serverStatus.download = true; break;
            case -2: serverStatus.playable = true; serverStatus.version = false; break;
            case -3: serverStatus.update = true; break;
            case 0: serverStatus.playable = true;
        }
        Download download = downloadManager.getDownloadStatus(game);
        if(download == null)
            return serverStatus;
        if(download.receivedParts < download.totalParts){
            serverStatus.downloading = true;
            serverStatus.downloadProgress = download.downloadProgress;
        }else{
            serverStatus.unzipping = true;
            serverStatus.unzipProgress = download.unzipProgress;
        }
        return serverStatus;
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

    public boolean startGame(Game game){
        if(game.isUptodate() != 0 && game.isUptodate() != -2){
            download(game);
            return false;
        }
        String start = "start ";
        if(game.getParam().equals(""))
            start += game.getExeFileRelative().substring(1);
        else
            start += game.getExeFileRelative().substring(1) + " " + game.getParam();
        return startProcess(game, start);
    }

    public boolean connect(Game game, String ip){
        if(game.isUptodate() != 0 && game.isUptodate() != -2) {
            download(game);
            return false;
        }
        if(!game.isConnectDirect())
            return false;
        String start;
        if(game.getParam().equals(""))
            start = "start " + game.getExeFileRelative().substring(1);
        else
            start = "start " + game.getExeFileRelative().substring(1) + " " + game.getParam();
        String parameterserver = game.getConnectParam().replace("?", ip);
        File file = new File(Objects.requireNonNull(GameFolderHelper.getAbsolutePath(game.getExeFileRelative())));
        try {
            ProcessBuilder process = new ProcessBuilder("cmd.exe", "/C", start + " " + parameterserver);
            process.directory(file.getParentFile());
            process.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean startServer(Game game, String param){
        if(game.isUptodate() != 0 && game.isUptodate() != -2) {
            download(game);
            return false;
        }
        if(!game.getOpenServer())
            return false;
        String start;
        if(param.equals(""))
            start = "start " + game.getExeServerRelative().substring(1);
        else
            start = "start " + game.getExeServerRelative().substring(1) + " " + param;
        return startProcess(game, start);
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
        File file = new File(Objects.requireNonNull(GameFolderHelper.getAbsolutePath(game.getExeFileRelative())));
        try {
            ProcessBuilder process = new ProcessBuilder("cmd.exe", "/C", start);
            process.directory(file.getParentFile());
            process.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean changeUsername(String username){
        if(user.getUsername().equals(username)) {
            System.err.println("Username is allready " + username);
            return false;
        }else if(!isConnected()){
            System.err.println("No connection to the server.");
        }
        System.out.println("Changed name from " + user.getUsername() + " to " + username);
        if(!PropertyHelper.setUserName(username))
            return false;
        user.setName(username);
        sendTCP(new UserupdateMessage(user));
        return true;
    }

    private boolean changeGamepath(String gamepath){
        if(user.getGamepath().equals(gamepath)) {
            System.err.println("Gamepath is allready " + gamepath);
            return false;
        }else if(!isConnected()){
            System.err.println("No connection to the server.");
        }
        if(!gamepath.endsWith("\\"))
            gamepath += "\\";
        System.out.println("Changed path from " + user.getGamepath() + " to " + gamepath);
        if(!PropertyHelper.setGamePath(gamepath))
            return false;
        user.setGamepath(gamepath);
        return true;
    }

    */

}
