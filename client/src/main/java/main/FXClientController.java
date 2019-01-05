package main;

import client.MyClient;
import entities.Game;
import entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fx")
public class FXClientController {
    private MyClient client = Main.client;

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

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public ResponseEntity<?> getGames(){
        return new ResponseEntity<>(client.getGames(), HttpStatus.OK);
    }

    @RequestMapping(value = "/games/uptodate", method = RequestMethod.POST)
    public Boolean isUptodate(@RequestBody Game game){
        return game.isUptodate();
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers(){
        return new ResponseEntity<>(client.getUsers(), HttpStatus.OK);
    }

/*
    Client client = Main.client;

    @RequestMapping(value="/status", method= RequestMethod.GET)
    public String status(){
        return client.status();
    }

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public void updateProperties(){
        client.update();
    }

    @RequestMapping("/games")
    public ResponseEntity<?> games(){
        List<Game> gamelist = client.getGamelist();
        return new ResponseEntity<>(gamelist, HttpStatus.OK);
    }

    @RequestMapping("/users")
    public ResponseEntity<?> users(){
        HashMap<Integer, User> usermap = client.getUserlist();
        List<User> userlist = new ArrayList<>();
        usermap.values().forEach(user -> {
            userlist.add(user);
        });
        return new ResponseEntity<>(userlist, HttpStatus.OK);
    }

    @RequestMapping("/games/{name}/isuptodate")
    public Boolean getVersion(@PathVariable String name){
        Game game = null;
        for(Game gameIter : client.getGamelist()){
            if(gameIter.getName().equals(name)){
                game = gameIter;
            }
        }
        return game.isUpToDate();
    }

    @RequestMapping("/download")
    public int download(@RequestParam(name="game") String gameName){
        return client.downloadGame(gameName);
    }
    */
}
