package dev.matichelo.service.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient //esta anotación permite que el servicio se registre en un servidor de descubrimiento como Eureka o Consul, pero no es estrictamente necesaria si se utiliza Spring Cloud Netflix Eureka, ya que Spring Boot la configura automáticamente.
@EnableFeignClients
public class ServiceUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }

}
