package client.monitor;

import entities.game.Game;

import java.util.ArrayList;

public class GameStatusList extends ArrayList<GameStatus> {

    public GameStatus get(Game game){
        for(GameStatus gameStatus : this) {
            if (gameStatus.getGame().equals(game)){
                return gameStatus;
            }
        }
        return null;
    }

}
