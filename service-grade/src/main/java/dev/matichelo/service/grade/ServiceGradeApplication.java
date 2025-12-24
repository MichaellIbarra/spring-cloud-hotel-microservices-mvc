package dev.matichelo.service.grade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceGradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGradeApplication.class, args);
    }

}
