package main;

import com.esotericsoftware.minlog.Log;
import entities.Game;
import entities.User;
import helper.NoKryoLogging;
import org.apache.log4j.Logger;
import server.MyServer;

import java.io.File;
import java.util.Scanner;

/**
 * Class that starts the LAN server for the lanpartymanager.
 */
public final class LanServer {
    private static Logger log = Logger.getLogger(LanServer.class);
    private static MyServer server;


    /**
     * Main-method.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        //Startup sequence. Get folder with zipped game files from the user.
        System.out.println("Lanpartymanager - Server");
        File gamepath = getGameFolder();

        //Turn off KryoNet logging
        Log.setLogger(new NoKryoLogging());
        //Start Server
        server = new MyServer(gamepath);
        server.start();

        printGames();
        userInput();
    }

    /**
     * Listen on user inputs in a new {@link Thread}.
     */
    private static void userInput(){
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true){
                //Input parameter list
                System.out.println("\nINPUT: [rebuildgames] [games] [game #] [users] [user #] [restart] [exit]");

                //Get user input and split it for arguments with multiple input
                String[] inputs = scanner.nextLine().trim().split(" ");
                //Get main argument
                String argument = inputs[0];

                //Choose right action to do for the provided main argument
                if(argument.equals("rebuildgames")){
                    server.updateGames();
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
                else if(argument.equals("restart")){
                    server.stop();
                    server.close();
                    server = new MyServer(server);
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
     * Prints all game names with a preceded number.
     */
    private static void printGames(){
        for(int i = 0; i < server.getGames().size(); i++){
            System.out.println("(" + (i+1) + ") " + server.getGames().get(i));
        }
    }

    /**
     * Prints details of a game. Printed are the name, filename on the server, version, command-line arguments,
     * connection command-line arguments, server command-line arguments, .exe file name, server .exe file name,
     * direct ip connection, open server, size of the game file [bytes].
     *
     * @param gamenumber game that should be printed in detail.
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
        System.out.println("SIZE [BYTE]: " + game.getSizeServer());
    }

    /**
     * Prints all user names with a preceded number.
     */
    private static void printUsers(){
        if(server.getUsersAsList().isEmpty()) {
            System.out.println("No users logged in.");
        }
        for(int i = 0; i < server.getUsersAsList().size(); i++){
            System.out.println("(" + (i+1) + ") " + server.getUsersAsList().get(i));
        }
    }

    /**
     * Prints details of a user. Printed are the name, ip-address, gamepath, order.
     *
     * @param usernumber user that should be printed in detail.
     */
    private static void printUsersDetails(int usernumber){
        User user = server.getUsersAsList().get(usernumber);

        System.out.println("USERNAME: " + user.getUsername());
        System.out.println("IP-ADDRESS: " + user.getIpAddress());
        System.out.println("GAMEPATH: " + user.getGamepath());
        if(user.getOrder() == null)
            System.out.println("ORDER:");
        else
            System.out.println("ORDER: " + user.getOrder());
    }

    /**
     * Loop until the user enters a directory.
     *
     * @return {@link File} of the entered directory.
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