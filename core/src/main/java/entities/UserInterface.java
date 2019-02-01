package entities;

public interface UserInterface {
    String getUsername();
    boolean setUsername(String username);
    String getGamepath();
    boolean setGamepath(String gamepath);
    String getIpAddress();
    boolean equals(UserInterface user);
    String toString();
}
