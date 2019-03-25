package client.monitor;

import client.LANClient;
import entities.game.Game;

import java.util.ArrayList;
import java.util.List;

public abstract class Monitor extends ArrayList<GameProcess> {
    LANClient client;


    @Override
    public boolean add(GameProcess gameProcess) {
        if(!gameProcess.isOpen())
            return false;
        gameProcess.setManger(this);
        return super.add(gameProcess);
    }

    public List<Game> getRunningProcesses(){
        List<Game> games = new ArrayList<>();
        for(GameProcess process : this){
            games.add(process.getGame());
        }
        return games;
    }

    abstract boolean update(GameProcess gameProcess);
}
