package springboot;

import client.MyClient;
import entities.User;
import main.LanClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fx")
public class FXAppInterface {
    private static final MyClient client = LanClient.client;

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

    /*
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<?> status(){
        return new ResponseEntity<>(client.getStatus(), HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public User getLogin(){
        return client.getUser();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Boolean login(@RequestBody User user){
        return client.updateUser(user);
    }

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<?> getGames(@RequestBody List<Game> games){
        return new ResponseEntity<>(client.getNewGames(games), HttpStatus.OK);
    }

    @RequestMapping(value = "/games/status", method = RequestMethod.POST)
    public GameStatus getStatus(@RequestBody Game game){
        return client.getGameStatus(game);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> getUsers(@RequestBody List<User> users){
        return new ResponseEntity<>(client.getNewUsers(users), HttpStatus.OK);
    }

    @RequestMapping(value = "/games/download", method = RequestMethod.POST)
    public Integer download(@RequestBody Game game){
        return client.download(game);
    }

    @RequestMapping(value = "/games/openexplorer", method = RequestMethod.POST)
    public Boolean openExplorer(@RequestBody Game game){
        return client.openExplorer(game);
    }

    @RequestMapping(value = "/games/startgame", method = RequestMethod.POST)
    public Boolean startGame(@RequestBody Game game){
        return client.startGame(game);
    }

    @RequestMapping(value = "/games/connect/{ip}", method = RequestMethod.POST)
    public Boolean connect(@RequestBody Game game, @PathVariable("ip") String ip){
        return client.connect(game, ip);
    }

    @RequestMapping(value = "/games/startserver", method = RequestMethod.POST)
    public Boolean startServer(@RequestBody Game game, @RequestParam("param") String param){
        return client.startServer(game, param);
    }
    */
}
