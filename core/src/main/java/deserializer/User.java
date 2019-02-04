package deserializer;

import entities.UserInterface;

public final class User implements UserInterface {
    private String username, gamepath, ipAddress;


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean setUsername(String username) {
        this.username = username;
        return true;
    }

    @Override
    public String getGamepath() {
        return gamepath;
    }

    @Override
    public boolean setGamepath(String gamepath){
        this.gamepath = gamepath;
        return true;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public boolean equals(UserInterface user) {
        return getUsername().equals(user.getUsername()) && getIpAddress().equals(user.getIpAddress());
    }
}
