package main;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import entities.Game;
import requests.gameListRequest;

import java.io.IOException;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        //GameList gameList = new GamesList();

        Server server = new Server();
        //Register classes
        Kryo kryo = server.getKryo();
        kryo.register(Game.class);
        //Start server
        server.start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(new Listener() {
            public void received(Connection connection, Object object){
                if(object instanceof gameListRequest){
                    gameListRequest request = (gameListRequest)object;
                   // connection.sendTCP(gameList);
                }
            }
        });
    }
}
