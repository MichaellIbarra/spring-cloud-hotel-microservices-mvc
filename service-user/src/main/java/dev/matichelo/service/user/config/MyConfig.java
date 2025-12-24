package dev.matichelo.service.user.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // es para indicar que es una clase de configuración, gracias a esto Spring la detecta y la carga
public class MyConfig {

    @Bean // es para indicar que el método devuelve un bean que debe ser gestionado por el contenedor de Spring
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
