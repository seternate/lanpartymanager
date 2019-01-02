package main;

import client.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static Client client;

    public static void main(String[] args) {
        client = new Client();
        client.start();
        SpringApplication.run(Main.class, args);
    }
}
