package client.monitor;

import java.util.ArrayList;

public abstract class Monitor extends ArrayList<GameProcess> {

    @Override
    public boolean add(GameProcess gameProcess) {
        if(!gameProcess.isOpen())
            return false;
        gameProcess.setManger(this);
        return super.add(gameProcess);
    }

    abstract boolean update(GameProcess gameprocess);
}
