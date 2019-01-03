package main;

import client.Client;
import entities.Game;
import entities.User;
import helper.PropertiesHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@RestController
public class ClientController {

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
}
