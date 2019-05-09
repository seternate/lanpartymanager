package message;

import entities.game.GameList;

/**
 * {@code GamelistMessage} is a class to send a {@link GameList}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class GamelistMessage {
    public GameList games;

    public GamelistMessage(){ }

    public GamelistMessage(GameList games){
        this.games = games;
    }

}
