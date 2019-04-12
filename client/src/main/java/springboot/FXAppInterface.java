package springboot;

import client.LANClient;
import entities.game.Game;
import entities.user.User;
import main.LanClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * Class for Springboot communication with the FXApp GUI.
 */
@RestController
@RequestMapping("/fx")
public class FXAppInterface {
    private static final LANClient client = LanClient.client;


    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity status(){
        return new ResponseEntity<>(client.getStatus(), HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity getUser(){
        return new ResponseEntity<>(client.getUser(), HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public boolean setUser(@RequestBody User user) {
        return client.updateUser(user);
    }

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public ResponseEntity getGames(){
        return new ResponseEntity<>(client.getGames(), HttpStatus.OK);
    }

    @RequestMapping(value = "/startgame", method = RequestMethod.POST)
    public boolean startGame(@RequestBody Game game){
        return client.startGame(game, true);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public int download(@RequestBody Game game){
        return client.download(game);
    }

    @RequestMapping(value = "/games/status", method = RequestMethod.POST)
    public ResponseEntity getGameStatus(@RequestBody Game game){
        return new ResponseEntity<>(client.getGameStatus(game), HttpStatus.OK);
    }

    @RequestMapping(value = "/openexplorer", method = RequestMethod.POST)
    public boolean openExplorer(@RequestBody Game game){
        return client.openExplorer(game);
    }

    @RequestMapping(value="/users", method = RequestMethod.GET)
    public ResponseEntity getUserlist(){
        return new ResponseEntity<>(client.getUserList(), HttpStatus.OK);
    }

    @RequestMapping(value = "/startserver", method = RequestMethod.POST)
    public Boolean startServer(@RequestBody Game game, @RequestParam("param") String parameters){
        return client.startServer(game, parameters, true);
    }

    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public Boolean connect(@RequestBody Game game, @RequestParam("ip") String ip){
        return client.connectServer(game, ip, true);
    }

    @RequestMapping(value = "/sendfiles", method = RequestMethod.POST)
    public Boolean sendFiles(@RequestBody User user, @RequestParam("files") List<File> files){
        return client.sendFiles(user, files);
    }

    @RequestMapping(value = "/filestatus", method = RequestMethod.GET)
    public Boolean getFileStatus(){
        return client.getDropFileDownloadStatus();
    }

    @RequestMapping(value = "/stopdownloadunzip", method = RequestMethod.POST)
    public Boolean stopDownloadUnzip(@RequestBody Game game){
        return client.stopDownloadUnzip(game);
    }

    @RequestMapping(value = "/getuserrungames", method = RequestMethod.GET)
    public ResponseEntity getUserRunGames(){
        return new ResponseEntity<>(client.getUserRunGames(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getuserrunservers", method = RequestMethod.GET)
    public ResponseEntity getUserRunServer(){
        return new ResponseEntity<>(client.getUserRunServer(), HttpStatus.OK);
    }

    @RequestMapping(value = "/stopgame", method = RequestMethod.POST)
    public Boolean stopGame(@RequestBody Game game){
        return client.stopGame(game);
    }

}
