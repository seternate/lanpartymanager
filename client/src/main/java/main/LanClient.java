package main;

import client.MyClient;
import com.esotericsoftware.minlog.Log;
import helper.NoKryoLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@ComponentScan(basePackages="springboot")
public class LanClient {
    public static MyClient client;

    public static void main(String[] args) {
        Log.setLogger(new NoKryoLogging());
        client = new MyClient();
        client.start();

        SpringApplication.run(LanClient.class, args);
    }

}
