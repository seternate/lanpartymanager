package main;

import entities.Game;
import server.MyServer;

import java.util.Scanner;

public final class LanServer {
    private static MyServer server;


    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("USAGE: LanServer [gamepath]");
            System.exit(-1);
        }
        String gamepath = args[0];

        server = new MyServer(gamepath);
        server.start();

        printGames();
        userInput();
    }

    private static void userInput(){
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true){
                System.out.println("\nInput: [rebuildgames] [games] [game -#] [users] [restart] [exit]");

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
                        System.out.println("Wrong arguments provided!");
                    }
                }

                else if(arg.equals("users"))
                    printUsers();

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
                    System.out.println("Wrong argument provided!");
            }
        }).start();
    }

    private static void printGamesDetails(int gamenumber){
        Game game = server.getGames().get(gamenumber);

        System.out.println("Name: " + game.getName());
        System.out.println("Filename Server: " + game.getServerFileName());
        System.out.println("Version: " + game.getVersionServer());
        System.out.println("Parameter: " + game.getParam());
        System.out.println("Connection Parameter: " + game.getConnectParam());
        System.out.println("Server Parameter: " + game.getServerParam());
        System.out.println("Exe: " + game.getExeFileRelative());
        System.out.println("Server Exe: " + game.getExeServerRelative());
        System.out.println("Direct connection: " + game.isConnectDirect());
        System.out.println("Open server: " + game.getOpenServer());
        System.out.println("Size: " + game.getSizeServer());
    }

    private static void printGames(){
        for(int i = 0; i < server.getGames().size(); i++){
            System.out.println("(" + (i+1) + ") " + server.getGames().get(i));
        }
    }

    private static void printUsers(){
        if(server.getUsersAsList().isEmpty()) {
            System.out.println("No users logged in.");
        }
        server.getUsersAsList().forEach(System.out::println);
    }
}
