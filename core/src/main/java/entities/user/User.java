package entities.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import deserialize.UserDeserializer;
import entities.settings.ClientSettings;
import helper.NetworkHelper;

import java.io.*;
import java.net.UnknownHostException;

/**
 * {@code User} manages all information from the {@link ClientSettings} and more.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
@JsonDeserialize(using = UserDeserializer.class)
public final class User {
    private ClientSettings settings;
    private String ipAddress;
    private String order;


    /**
     * Creates the {@code User}.
     *
     * @since 1.0
     */
    public User(){ }

    /**
     * Creates the {@code User} with the {@code settings}. The ip-address and order of the {@code User} are set.
     *
     * @param settings {@link ClientSettings} to use for the {@code User}
     * @throws UnknownHostException if a problem while getting the ip-address occurs
     * @since 1.0
     */
    public User(ClientSettings settings) throws UnknownHostException {
        this.settings = settings;
        ipAddress = NetworkHelper.getIPAddress();
        order = "";
    }

    /**
     * @return username of the {@User}
     * @since 1.0
     */
    public String getUsername(){
        return settings.getUsername();
    }

    /**
     * @param username username of the {@code User}
     * @since 1.0
     */
    public void setUsername(String username){
        settings.setUsername(username);
    }

    /**
     * @return gamepath of the {@code User}
     * @since 1.0
     */
    public String getGamepath(){
        return settings.getGamepath();
    }

    /**
     * @param gamepath gamepath of the {@code User}
     * @since 1.0
     */
    public void setGamepath(String gamepath){
        settings.setGamepath(gamepath);
    }

    /**
     * @return ip-address of the {@code User}
     * @since 1.0
     */
    public String getIpAddress(){
        return ipAddress;
    }

    /**
     * @param ipAddress ip-address of the {@code User}
     * @since 1.0
     */
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    /**
     * @return food-order of the {@code User}
     * @since 1.0
     */
    public String getOrder(){
        return order;
    }

    /**
     * @param order food-order of the {@code User}
     */
    public void setOrder(String order){
        this.order = order;
    }

    /**
     * Copies all information from {@code user} to the {@link User}.
     *
     * @param user {@link User} to copy its information
     * @return <b>true</b> if any information updated, else <b>false</b>
     * @throws IOException if any error while saving the updated {@code User} occurs
     * @since 1.0
     */
    public boolean update(User user) throws IOException {
        if(!this.equals(user)){
            if(!user.getUsername().isEmpty())
                setUsername(user.getUsername());
            if(!user.getGamepath().isEmpty())
                setGamepath(user.getGamepath());
            if(!user.getIpAddress().isEmpty())
                setIpAddress(user.getIpAddress());
            setOrder(user.getOrder());
            settings.save();
        }else{
            return false;
        }
        return true;
    }

    /**
     * Compares between this {@code User} and the {@code user} if they ave the same {@code username}, {@code gamepath},
     * {@code ip-address} and the {@code food-order}.
     *
     * @param user {@code User} to compare with
     * @return <b>true</b> if they have the same fields, else false
     * @since 1.0
     */
    public boolean equals(User user){
        return getUsername().equals(user.getUsername()) && getGamepath().equals(user.getGamepath())
                && getIpAddress().equals(user.getIpAddress())
                && getOrder().equals(user.getOrder());
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof User)
            return equals((User)o);
        return super.equals(o);
    }

    /**
     * @since 1.0
     */
    @Override
    public int hashCode(){
        return getUsername().hashCode() + getGamepath().hashCode() + getIpAddress().hashCode() + getOrder().hashCode();
    }

    /**
     * @return username of the {@code User}
     */
    @Override
    public String toString(){
        return getUsername();
    }

}
