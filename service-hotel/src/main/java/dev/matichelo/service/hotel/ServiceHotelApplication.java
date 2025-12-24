package dev.matichelo.service.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHotelApplication.class, args);
    }

}
