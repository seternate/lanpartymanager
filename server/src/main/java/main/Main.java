package main;

import server.MyServer;

import java.io.File;
import java.util.Scanner;

public final class Main {
    private static MyServer server;


    public static void main(String[] args) {
        if(args.length != 1)
            System.out.println("USAGE");
        String pathCompressed = args[0];
        File cFile = new File(pathCompressed);
        if(cFile.exists() && cFile.isDirectory()){
            server = new MyServer(cFile.getAbsolutePath());
            server.start();
        }else{
            System.out.println("Directory does not exist: " + pathCompressed);
        }
        userInput();
    }

    private static void userInput(){
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;
            while(!exit){
                System.out.println("Input: [rebuildgames] [games] [users] [restart] [exit]");
                String[] inputs = scanner.nextLine().split(" ");
                for(String input : inputs){
                    if(input.equals("rebuildgames"))
                        server.updateGames();
                    if(input.equals("games"))
                        server.printGames();
                    if(input.equals("users"))
                        server.printUsers();
                    if(input.equals("restart")){
                        server.close();
                        server.stop();
                        server = new MyServer(server.getGamepath());
                        server.start();
                    }
                    if(input.equals("exit"))
                        exit = true;
                }
            }
            server.close();
            server.stop();
        }).start();
    }
}
