package controller;

import clientInterface.Client;
import entities.game.Game;
import entities.game.GameList;
import clientInterface.GameStatusProperty;
import entities.server.ServerStatus;
import entities.user.User;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import stages.*;

import java.io.File;
import java.util.List;

/**
 * ApplicationManager manages all communication between the stages and between the GUI and the client.
 */
public class ApplicationManager {
    private static PreloaderStage preloaderStage;
    private static LoginStage loginStage;
    private static MainStage mainStage;
    private static UsersStage usersStage;
    private static OrderStage orderStage;
    private static ServerStartStage serverstartstage;
    private static ServerConnectStage serverconnectstage;
    private static Client client;

    /**
     * Called from main-method to start the application and the client.
     */
    public static void start(){
        preloaderStage = new PreloaderStage();
        preloaderStage.show();
        client = new Client();
    }

    /**
     * Called after client connected once to the client-application and opens the login stage.
     */
    public static void openLoginStage(){
        loginStage = new LoginStage();
        loginStage.show();
        preloaderStage.hide();
    }

    /**
     * Called after login stage and sends username and gamepath to the client. Opens the main stage and build the user,
     * order and setting stage.
     *
     * @param username username from the login stage.
     * @param gamepath gampath from the login stage.
     */
    public static void openMainStage(String username, String gamepath){
        client.sendUserData(username, gamepath, getUser().getOrder());
        mainStage = new MainStage();
        usersStage = new UsersStage();
        orderStage = new OrderStage();
        mainStage.show();
        loginStage.hide();
        loginStage = new LoginStage();
    }

    /**
     * Shows the user stage.
     */
    public static void showUsers(){
        if(usersStage.isShowing())
            usersStage.requestFocus();
        else
            usersStage.show();
    }

    /**
     * Shows the settings stage.
     */
    public static void showSettings(){
        if(loginStage.isShowing())
            loginStage.requestFocus();
        else
            loginStage.show();
    }

    /**
     * Shows the food ordering stage.
     */
    public static void showOrder(){
        if(orderStage.isShowing())
            orderStage.requestFocus();
        else
            orderStage.show();
    }

    /**
     * Called from main stage to proper exit the application.
     */
    public static void closeAllMainStages(){
        usersStage.hide();
        loginStage.hide();
        orderStage.hide();
    }

    /**
     * Called from the client-thread to check if the application is running.
     *
     * @return true if the application is running, else false.
     */
    public static boolean isRunning(){
        return preloaderStage.isShowing() || loginStage.isShowing() || mainStage.isShowing();
    }

    /**
     * Determine PreloaderStage showing status.
     *
     * @return true if preloaderStage is showing, else false.
     */
    public static boolean isPreloader(){
        return preloaderStage.isShowing();
    }

    /**
     * Determine if main stage is opened.
     *
     * @return true if mainstage is open, else false.
     */
    public static boolean isMainstage(){
        return mainStage != null;
    }

    /**
     * @return the status of the LANServer.
     */
    public static ServerStatus getServerStatus(){
        return client.getServerStatus();
    }

    /**
     * @return true if connected to the server, else false.
     */
    public static boolean isConnected(){
        return client.getServerStatus().isConnected();
    }

    /**
     * @return username of the user.
     */
    public static String getUsername(){
        return client.getUser().getUsername();
    }

    /**
     * @return gamepath of the user.
     */
    public static String getGamepath(){
        return client.getUser().getGamepath();
    }

    /**
     * Setting the serverStatus label, which is updated based on the server connection status.
     *
     * @param lblStatus label to be updated.
     */
    public static void setServerStatusLabel(Label lblStatus){
        client.setServerStatusLabel(lblStatus);
    }

    /**
     * Called from the settings stage to save username and gamepath.
     *
     * @param username username from the login stage.
     * @param gamepath gampath from the login stage.
     */
    public static void saveSettings(String username, String gamepath){
        client.sendUserData(username, gamepath, getUser().getOrder());
        loginStage.hide();
    }

    /**
     * Saves the order from the user.
     *
     * @param order placed order from the user.
     */
    public static void setOrder(String order){
        client.sendUserData(getUsername(), getGamepath(), order);
    }

    /**
     * Stops the download or extraction of the specified game.
     *
     * @param game game to stop downloading or extracting.
     */
    public static void stopDownloadUnzip(Game game){
        client.stopDownloadUnzip(game);
    }














    /**
     * Starts a game.
     * @param game game to be started.
     */
    public static void startGame(Game game){
        client.startGame(game);
    }

    /**
     * Downloads a game.
     *
     * @param game game to be downloaded.
     */
    public static void downloadGame(Game game){
        client.downloadGame(game);
    }

    /**
     * Opens the game folder in explorer.
     *
     * @param game game to be shown in the explorer.
     */
    public static void openExplorer(Game game){
        client.openExplorer(game);
    }

    /**
     * Shows the serverconnectstage.
     *
     * @param game game to connect to a server.
     */
    public static void openServerList(Game game){
        serverconnectstage = new ServerConnectStage(game);
        serverconnectstage.show();
    }

    /**
     * Show the serverstartstage.
     *
     * @param game game to start a server with.
     */
    public static void openServerStartup(Game game){
        serverstartstage = new ServerStartStage(game);
        serverstartstage.show();
    }

    /**
     * @return games available on the server.
     */
    public static GameList getGames(){
        return client.getGames();
    }

    /**
     * @return users logged in the server.
     */
    public static ObservableList<User> getUserslist(){
        return client.getUsersList();
    }

    /**
     * @return Focused game from the main stage.
     */
    public static Game getFocusedGame(){
        return mainStage == null ? null : mainStage.getFocusedGame();
    }

    /**
     * @return gamestatus from the focused game.
     */
    public static GameStatusProperty getGamestatusProperty(){
        return client.getGamestatusProperty();
    }

    /**
     * Updates main gametiles after new games available on the server.
     */
    public static void updateMainstageRoot(){
        if(mainStage == null)
            return;
        mainStage.updateRoot();
    }

    /**
     * Starting a server.
     *
     * @param game game from which should a server be started.
     * @param parameters start parameters.
     */
    public static void startServer(Game game, String parameters){
        client.startServer(game, parameters);
        serverstartstage.hide();
    }

    /**
     * Connect to a server.
     *
     * @param game game which should be connected to.
     * @param ip ip address of the user with the open server.
     */
    public static void connectServer(Game game, String ip){
        client.connectServer(game, ip);
        serverconnectstage.hide();
    }




    public static User getUser(){
        return client.getUser();
    }



    public static ObservableList<User> getOrderList(){
        return client.getOrderList();
    }

    public static void sendFiles(User user, List<File> files){
        client.sendFiles(user, files);
    }

    public static void setFileStatusLabel(Label lblFileStatus){
        client.setFileStatusLabel(lblFileStatus);
    }
}
