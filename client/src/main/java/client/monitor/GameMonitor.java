package client.monitor;

import java.util.ArrayList;

public class GameMonitor extends ArrayList<GameProcess> {

    public GameMonitor(){ }

    @Override
    public boolean add(GameProcess gameProcess) {
        if(!gameProcess.isOpen())
            return false;
        gameProcess.setManger(this);
        return super.add(gameProcess);
    }
}
