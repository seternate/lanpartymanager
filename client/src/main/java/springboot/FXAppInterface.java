package springboot;

import client.LANClient;
import entities.game.Game;
import entities.user.User;
import main.LanClient;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * {@code FXAppInterface} is the {@link RestController} of the {@link SpringApplication} for any {@code FXGui} requests.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/fx")
public class FXAppInterface {
    private static final LANClient client = LanClient.client;


    /**
     * @see LANClient#getStatus()
     * @since 1.0
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity status(){
        return new ResponseEntity<>(client.getStatus(), HttpStatus.OK);
    }

    /**
     * @see LANClient#getUser()
     * @since 1.0
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity getUser(){
        return new ResponseEntity<>(client.getUser(), HttpStatus.OK);
    }

    /**
     * @see LANClient#updateUser(User)
     * @since 1.0
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public boolean setUser(@RequestBody User user) {
        return client.updateUser(user);
    }

    /**
     * @see LANClient#getGames()
     * @since 1.0
     */
    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public ResponseEntity getGames(){
        return new ResponseEntity<>(client.getGames(), HttpStatus.OK);
    }

    /**
     * @see LANClient#startGame(Game, boolean)
     * @since 1.0
     */
    @RequestMapping(value = "/startgame", method = RequestMethod.POST)
    public boolean startGame(@RequestBody Game game){
        return client.startGame(game, true);
    }

    /**
     * @see LANClient#download(Game)
     * @since 1.0
     */
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public int download(@RequestBody Game game){
        return client.download(game);
    }

    /**
     * @see LANClient#getGameStatus(Game)
     * @since 1.0
     */
    @RequestMapping(value = "/games/status", method = RequestMethod.POST)
    public ResponseEntity getGameStatus(@RequestBody Game game){
        return new ResponseEntity<>(client.getGameStatus(game), HttpStatus.OK);
    }

    /**
     * @see LANClient#openExplorer(Game)
     * @since 1.0
     */
    @RequestMapping(value = "/openexplorer", method = RequestMethod.POST)
    public boolean openExplorer(@RequestBody Game game){
        return client.openExplorer(game);
    }

    /**
     * @see LANClient#getUserList()
     * @since 1.0
     */
    @RequestMapping(value="/users", method = RequestMethod.GET)
    public ResponseEntity getUserlist(){
        return new ResponseEntity<>(client.getUserList(), HttpStatus.OK);
    }

    /**
     * @see LANClient#startServer(Game, String, boolean)
     * @since 1.0
     */
    @RequestMapping(value = "/startserver", method = RequestMethod.POST)
    public Boolean startServer(@RequestBody Game game, @RequestParam("param") String parameters){
        return client.startServer(game, parameters, true);
    }

    /**
     * @see LANClient#connectServer(Game, String, boolean)
     * @since 1.0
     */
    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public Boolean connect(@RequestBody Game game, @RequestParam("ip") String ip){
        return client.connectServer(game, ip, true);
    }

    /**
     * @see LANClient#sendFiles(User, List)
     * @since 1.0
     */
    @RequestMapping(value = "/sendfiles", method = RequestMethod.POST)
    public Boolean sendFiles(@RequestBody User user, @RequestParam("files") List<File> files){
        return client.sendFiles(user, files);
    }

    /**
     * @see LANClient#getDropFileDownloadStatus()
     * @since 1.0
     */
    @RequestMapping(value = "/filestatus", method = RequestMethod.GET)
    public Boolean getFileStatus(){
        return client.getDropFileDownloadStatus();
    }

    /**
     * @see LANClient#stopDownloadUnzip(Game)
     * @since 1.0
     */
    @RequestMapping(value = "/stopdownloadunzip", method = RequestMethod.POST)
    public Boolean stopDownloadUnzip(@RequestBody Game game){
        return client.stopDownloadUnzip(game);
    }

    /**
     * @see LANClient#getUserRunGames()
     * @since 1.0
     */
    @RequestMapping(value = "/getuserrungames", method = RequestMethod.GET)
    public ResponseEntity getUserRunGames(){
        return new ResponseEntity<>(client.getUserRunGames(), HttpStatus.OK);
    }

    /**
     * @see LANClient#getUserRunServer()
     * @since 1.0
     */
    @RequestMapping(value = "/getuserrunservers", method = RequestMethod.GET)
    public ResponseEntity getUserRunServer(){
        return new ResponseEntity<>(client.getUserRunServer(), HttpStatus.OK);
    }

    /**
     * @see LANClient#stopGame(Game)
     * @since 1.0
     */
    @RequestMapping(value = "/stopgame", method = RequestMethod.POST)
    public Boolean stopGame(@RequestBody Game game){
        return client.stopGame(game);
    }

}
