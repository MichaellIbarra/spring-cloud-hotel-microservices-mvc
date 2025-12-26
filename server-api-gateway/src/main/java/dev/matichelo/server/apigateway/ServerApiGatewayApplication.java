package dev.matichelo.server.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // es importante para que el gateway se registre en el servicio de descubrimiento
public class ServerApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApiGatewayApplication.class, args);
    }

}
