package Main;

import entities.Game;

public class TestVersion {
    public static void main(String[] args) {
        Game game = new Game("F:\\Destiny 2\\", "destiny2.exe");
        System.out.println(game.getName());
    }
}
