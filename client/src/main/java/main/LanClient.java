package main;

import client.MyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.Thread.sleep;

@SpringBootApplication
public class LanClient {
    static MyClient client;


    public static void main(String[] args) {
        client = new MyClient();
        while(!client.isConnected()){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SpringApplication.run(LanClient.class, args);
    }
}
