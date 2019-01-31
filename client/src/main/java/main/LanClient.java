package main;

import client.MyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="springboot")
public class LanClient {
    public static MyClient client;


    public static void main(String[] args) {
        client = new MyClient();
        client.start();
        SpringApplication.run(LanClient.class, args);
    }
}
