package client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import entities.Game;
import entities.GameStatus;
import entities.ServerStatus;
import entities.User;
import helper.GameFolderHelper;
import helper.NetworkClassRegistrationHelper;
import helper.PropertiesHelper;
import message.*;
import requests.DownloadRequest;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyClient extends com.esotericsoftware.kryonet.Client {
    private List<Game> games;
    private Map<Integer, User> users;
    private User user;
    private ServerStatus status;
    public DownloadManager downloadManager;


    public MyClient(){
        super();
        downloadManager = new DownloadManager();
        user = new User();
        status = new ServerStatus();
        NetworkClassRegistrationHelper.registerClasses(this);
        registerListener();
        start();
    }

    @Override
    public void start(){
        super.start();
        new Thread(() -> {
            while(true){
                if(isConnected()) {
                    status.serverConnection = true;
                    status.serverIP = getRemoteAddressTCP().getHostString();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    status.serverConnection = false;
                    connect();
                }
            }
        }).start();
    }

    public boolean updateUser(User user){
        boolean changed = false;
        changed = changeUsername(user.getName()) || changeGamepath(user.getGamepath());
        return changed;
    }

    private boolean changeUsername(String username){
        if(user.getName().equals(username)) {
            System.err.println("Username is allready " + username);
            return false;
        }else if(!isConnected()){
            System.err.println("No connection to the server.");
        }
        System.out.println("Changed name from " + user.getName() + " to " + username);
        if(!PropertiesHelper.setUserName(username))
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
        System.out.println("Changed path from " + user.getGamepath() + " to " + gamepath);
        if(!PropertiesHelper.setGamePath(gamepath))
            return false;
        user.setGamepath(gamepath);
        sendTCP(new UserupdateMessage(user));
        return true;
    }

    public ServerStatus getStatus(){
        return status;
    }

    public User getUser(){
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
            System.out.println("Updated Users.");
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
        File sFile = new File(PropertiesHelper.getGamepath());
        if(game.getSizeServer() > sFile.getFreeSpace())
            return -2;
        int openport = getOpenPort();
        downloadManager.add(new Download(openport, game, game.getSizeServer()));
        System.out.println("Requested to download " + game.getName() + ".");
        sendTCP(new DownloadRequest(game, openport));
        return 0;
    }

    public GameStatus getGameStatus(Game game){
        GameStatus status = new GameStatus();
        int uptodate = game.isUptodate();
        switch(uptodate){
            case -1: status.download = true; break;
            case -2: status.playable = true; status.version = false; break;
            case -3: status.update = true; break;
            case 0: status.playable = true;
        }
        Download download = downloadManager.getDownloadStatus(game);
        if(download == null)
            return status;
        if(download.receivedParts < download.totalParts){
            status.downloading = true;
            status.downloadProgress = download.downloadProgress;
            System.out.println(download.downloadProgress);
        }else{
            status.unzipping = true;
            status.unzipProgress = download.unzipProgress;
        }
        return status;
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
        File file = new File(GameFolderHelper.getAbsolutePath(game.getExeFileRelative()));
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
        File file = new File(GameFolderHelper.getAbsolutePath(game.getExeFileRelative()));
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
        System.out.println("String " + game.getExeServerRelative());
        if(param.equals(""))
            start = "start " + game.getExeServerRelative().substring(1);
        else
            start = "start " + game.getExeServerRelative().substring(1) + " " + param;
        File file = new File(GameFolderHelper.getAbsolutePath(game.getExeFileRelative()));
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

    private int getOpenPort(){
        ServerSocket server = null;
        try {
            server = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int freeport = server.getLocalPort();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freeport;
    }

    private void registerListener(){
        addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                sendTCP(new LoginMessage(user));
                System.out.println("Logged in.");
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

    private void connect(){
        int tcp = PropertiesHelper.getServerTcp();
        int udp = PropertiesHelper.getServerUdp();
        InetAddress address = discoverHost(udp, 5000);
        if(address != null) {
            try {
                connect(500, address, tcp, udp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*



    public int downloadGame(String gameName){
        for (Game game : gamelist) {
            if(game.getName().equals(gameName)){
                return downloadGame(game);
            }
        }
        return 3;
    }



    public ArrayList<Game> getGamelist(){
        return this.gamelist;
    }

    public HashMap<Integer, User> getUserlist(){
        return this.userlist;
    }

    public String status(){
        return status;
    }

    public void update(){
        this.user = new User();
        File dFile = new File(PropertiesHelper.getGamepath());
        if(!dFile.exists()) dFile.mkdirs();
        client.sendTCP(user);
    }
    */
}
