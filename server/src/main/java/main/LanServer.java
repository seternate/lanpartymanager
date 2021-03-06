package main;

import com.esotericsoftware.minlog.Log;
import entities.game.Game;
import entities.user.User;
import helper.kryo.KryoLogging;
import org.apache.log4j.Logger;
import server.LANServer;
import server.upload.GameUpload;

import java.io.File;
import java.util.Scanner;

/**
 * {@code LanServer} starts the {@link LANServer} and console I/O.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class LanServer {
    private static Logger log = Logger.getLogger(LanServer.class);
    private static LANServer server;


    /**
     * @param args Command-line arguments
     * @since 1.0
     */
    public static void main(String[] args) {
        //Startup sequence. Get folder with zipped game files from the user.
        System.out.println("Lanpartymanager - Server");
        File gamepath = getGameFolder();

        //Turn off KryoNet logging
        Log.setLogger(new KryoLogging());
        //Start Server
        server = new LANServer(gamepath);
        server.start();

        printGames();
        userInput();
    }

    /**
     * Listen on user inputs in a new {@link Thread}.
     *
     * @since 1.0
     */
    private static void userInput(){
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true){
                //Input parameter list
                System.out.println("INPUT: [rebuildgames] [games] [game #] [users] [user #] [downloads] [download-stop #] [restart] [exit]");

                //Get user input and split it for arguments with multiple input
                String[] inputs = scanner.nextLine().trim().split(" ");
                //Get main argument
                String argument = inputs[0];

                //Choose right action to do for the provided main argument
                if(argument.equals("rebuildgames")){
                    server.reloadGames();
                    printGames();
                }
                else if(argument.equals("games"))
                    printGames();
                else if(argument.equals("game")){
                    int game;
                    try {
                        //Subtract 1 for array-bounds.
                        game = Integer.valueOf(inputs[1]) - 1;
                        printGamesDetails(game);
                    } catch (Exception e) {
                        log.warn("Wrong argument provided for 'game'. Please use 'games' for an existing game number.");
                        log.debug(e);
                    }
                }
                else if(argument.equals("users"))
                    printUsers();
                else if(argument.trim().equals("user")){
                    int user;
                    try {
                        user = Integer.valueOf(inputs[1]) - 1;
                        printUsersDetails(user);
                    } catch (Exception e) {
                        log.warn("Wrong argument provided for 'user'. Please use 'users' for an existing user number.");
                        log.debug(e);
                    }
                }
                else if(argument.equals("downloads"))
                    printDownloads();
                else if(argument.trim().equals("download-stop")){
                    int download;
                    try {
                        download = Integer.valueOf(inputs[1]) - 1;
                        stopDownload(download);
                    } catch(Exception e) {
                        log.warn("Wrong argument provided for 'download-stop'. Please use 'downloads' for an existing user number.");
                        log.debug(e);
                    }
                }
                else if(argument.equals("restart")){
                    server = server.restart();
                }
                else if(argument.equals("exit")){
                    server.close();
                    server.stop();
                    System.exit(0);
                }
                else
                    log.warn("Wrong argument provided.");
            }
        }).start();
    }

    /**
     * Prints all {@link Game} names with a preceded number.
     *
     * @since 1.0
     */
    private static void printGames(){
        for(int i = 0; i < server.getGames().size(); i++){
            System.out.println("(" + (i+1) + ") " + server.getGames().get(i));
        }
    }

    /**
     * Prints details of a {@link Game}. Printed are the name, filename on the server, version, command-line arguments,
     * connection command-line arguments, server command-line arguments, .exe file name, server .exe file name,
     * direct ip connection, open server, size of the game file [bytes].
     *
     * @param gamenumber number of the {@link Game} to print (see {@link #printGames()})
     * @since 1.0
     */
    private static void printGamesDetails(int gamenumber){
        Game game = server.getGames().get(gamenumber);

        System.out.println("NAME: " + game.getName());
        System.out.println("FILENAME ON SERVER: " + game.getServerFileName());
        System.out.println("VERSION: " + game.getVersionServer());
        System.out.println("COMMAND-LINE ARGUMENTS: " + game.getParam());
        System.out.println("CONNECTION CL-ARGUMENTS: " + game.getConnectParam());
        System.out.println("SERVER CL-ARGUMENTS: " + game.getServerParam());
        System.out.println("EXE-FILENAME: " + game.getExeFileRelative());
        System.out.println("SERVER EXE-FILENAME: " + game.getExeServerRelative());
        System.out.println("DIRECT IP CONNECTION: " + game.isConnectDirect());
        System.out.println("OPEN SERVER: " + game.isOpenServer());
        System.out.println("SIZE [BYTE]: " + new File(server.getGamedirectory(), game.getServerFileName()).length());
    }

    /**
     * Prints all {@link User} names with a preceded number and the amount of open games and servers.
     *
     * @since 1.0
     */
    private static void printUsers(){
        if(server.getUsers().isEmpty())
            System.out.println("No users logged in.");

        for(int i = 0; i < server.getUsers().size(); i++){
            User user = server.getUsers().get(i);
            System.out.println("(" + (i+1) + ") " + user + " : Games open - "
                    + server.getOpenGamesSize(user) + " : Servers open - " + server.getOpenServersSize(user));
        }
    }

    /**
     * Prints details of a {@link User}. Printed are the name, ip-address, gamepath, order, open games, open servers.
     *
     * @param usernumber number of the {@link User} to print (see {@link #printUsers()})
     * @since 1.0
     */
    private static void printUsersDetails(int usernumber){
        User user = server.getUsers().get(usernumber);

        System.out.println("USERNAME: " + user.getUsername());
        System.out.println("IP-ADDRESS: " + user.getIpAddress());
        System.out.println("GAMEPATH: " + user.getGamepath());
        if(user.getOrder() == null)
            System.out.println("ORDER:");
        else
            System.out.println("ORDER: " + user.getOrder());

        StringBuilder opengames = new StringBuilder();
        for(int i = 0; i < server.getOpenGamesSize(user) - 1; i++){
            opengames.append(server.getOpenGames(user).get(i) + ", ");
        }
        if(server.getOpenGamesSize(user) != 0)
            opengames.append(server.getOpenGames(user).get(server.getOpenGamesSize(user) - 1));
        System.out.println("OPEN GAMES: " + opengames.toString());

        StringBuilder openservers = new StringBuilder();
        for(int i = 0; i < server.getOpenServersSize(user) - 1; i++){
            openservers.append(server.getOpenServers(user).get(i) + ", ");
        }
        if(server.getOpenServersSize(user) != 0)
            openservers.append(server.getOpenServers(user).get(server.getOpenServersSize(user) - 1));
        System.out.println("OPEN SERVERS: " + openservers.toString());
    }

    /**
     * Prints running {@code Downloads}. Printed are the user, game, upload progress, average upload speed in
     * MByte/second.
     *
     * @since 1.0
     */
    private static void printDownloads(){
        if(server.getUploads().isEmpty())
            System.out.println("No running uploads.");

        for(int i = 0; i < server.getUploads().size(); i++){
            GameUpload upload = server.getUploads().get(i);
            System.out.println("(" + (i+1) + ") " + upload.getUser() + " : " + upload.getGame() + " ("
                    + upload.getProgress()*100 + "%) - " + (double)Math.round((double)upload.getAverageUploadspeed()/10485.76)/100. + " MByte/sec");
        }
    }

    /**
     * Stops a download.
     *
     * @param download number to be stopped (see {@link #printDownloads()})
     * @since 1.0
     */
    private static void stopDownload(int download){
        GameUpload upload = server.getUploads().get(download);
        server.stopUpload(upload);
    }

    /**
     * Loops until the user enters a directory.
     *
     * @return {@link File} of the entered directory
     * @since 1.0
     */
    private static File getGameFolder(){
        File gamedirectory;
        String gamepath;
        Scanner scanner = new Scanner(System.in);

        do{
            System.out.print("Please enter gamepath of the server: ");
            gamepath = scanner.nextLine();
            gamedirectory = new File(gamepath);
            if(!gamedirectory.isDirectory())
                log.error("'" + gamedirectory.getAbsolutePath() + "' is no directory or doesn't exist.");
        }while(!gamedirectory.isDirectory());

        return gamedirectory;
    }

}