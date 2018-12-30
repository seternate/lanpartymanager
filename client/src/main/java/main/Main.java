package main;

import client.Client;
import entities.Game;
import entities.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static Client client;

    public static void main(String[] args) {
        client = new Client();
        client.start();

        ArrayList<Game> gamelist = client.getGamelist();
        System.out.println("Gamelist: ");
        for(Game game : gamelist){
            System.out.println(game.getName());
        }

        HashMap<Integer, User> userlist = client.getUserlist();
        System.out.println("Userlist: ");
        for(User user : userlist.values()){
            System.out.println(user.toString());
        }
    }
}
