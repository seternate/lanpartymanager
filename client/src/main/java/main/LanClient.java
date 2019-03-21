package main;

import client.LANClient;
import com.esotericsoftware.minlog.Log;
import helper.kryo.NoKryoLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="springboot")
public class LanClient {
    public static LANClient client;

    public static void main(String[] args) {
        Log.setLogger(new NoKryoLogging());
        client = new LANClient();

        SpringApplication.run(LanClient.class, args);
    }

}
