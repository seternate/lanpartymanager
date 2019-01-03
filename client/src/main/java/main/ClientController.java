package main;

import client.Client;
import entities.Game;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientController {

    Client client = Main.client;

    @RequestMapping("/")
    public boolean index(){
        return true;
    }

    @RequestMapping("/games")
    public ResponseEntity<?> games(){
        List<Game> gamelist = client.getGamelist();
        return new ResponseEntity<List<Game>>(gamelist, HttpStatus.OK);
    }

    @RequestMapping("/download")
    public int download(@RequestParam(name="game") String gameName){
        return client.downloadGame(gameName);
    }
}
