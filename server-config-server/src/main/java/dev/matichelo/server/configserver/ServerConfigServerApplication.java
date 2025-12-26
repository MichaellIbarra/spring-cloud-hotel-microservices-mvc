package dev.matichelo.server.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ServerConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerConfigServerApplication.class, args);
    }

}
