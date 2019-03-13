package main;

import com.esotericsoftware.minlog.Log;
import entities.Game;
import entities.User;
import helper.NoKryoLogging;
import server.MyServer;

import java.util.Scanner;

public final class LanServer {
    private static MyServer server;


    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("USAGE: LanServer [gamepath]");
            System.exit(-1);
        }
        String gamepath = args[0];

        Log.setLogger(new NoKryoLogging());
        server = new MyServer(gamepath);
        server.start();
        System.out.println("SERVER: Started.\n");

        printGames();
        userInput();
    }

    private static void userInput(){
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true){
                System.out.println("\nINPUT: [rebuildgames] [games] [game -#] [users] [user -#] [restart] [exit]\n");

                String[] inputs = scanner.nextLine().split("-");

                if(inputs.length == 0)
                    continue;

                String arg = inputs[0];

                if(arg.equals("rebuildgames")){
                    server.updateGames();
                    printGames();
                }

                else if(arg.equals("games"))
                    printGames();

                else if(arg.trim().equals("game")){
                    int game;
                    try {
                        game = Integer.valueOf(inputs[1]) - 1;
                        printGamesDetails(game);
                    } catch (Exception e) {
                        System.err.println("ERROR: Wrong arguments provided! No such number!");
                    }
                }

                else if(arg.equals("users"))
                    printUsers();

                else if(arg.trim().equals("user")){
                    int user;
                    try {
                        user = Integer.valueOf(inputs[1]) - 1;
                        printUsersDetails(user);
                    } catch (Exception e) {
                        System.err.println("ERROR: Wrong arguments provided! No such number!");
                    }
                }

                else if(arg.equals("restart")){
                    server.stop();
                    server.close();
                    server = new MyServer(server);
                }

                else if(arg.equals("exit")){
                    server.close();
                    server.stop();
                    System.exit(0);
                }

                else
                    System.err.println("ERROR: Wrong argument provided!");
            }
        }).start();
    }

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

    private static void printGames(){
        for(int i = 0; i < server.getGames().size(); i++){
            System.out.println("(" + (i+1) + ") " + server.getGames().get(i));
        }
    }

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

    private static void printUsers(){
        if(server.getUsersAsList().isEmpty()) {
            System.out.println("No users logged in.");
        }
        for(int i = 0; i < server.getUsersAsList().size(); i++){
            System.out.println("(" + (i+1) + ") " + server.getUsersAsList().get(i));
        }
    }
}