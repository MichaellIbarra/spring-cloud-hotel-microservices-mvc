package dev.matichelo.server.apigateway.config;

import dev.matichelo.server.apigateway.dto.TokenDto;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private WebClient.Builder webClient;

    public AuthFilter(WebClient.Builder builder) {
        super(Config.class);
        this.webClient = builder;
    }


    @Override
    public GatewayFilter apply(AuthFilter.Config config) {
        return ((((exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String tokenHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String[] chunks = tokenHeader.split(" "); // Bearer xxx.yyy.zzz
            System.out.println(Arrays.toString(chunks));
            if(chunks.length != 2 || !chunks[0].equals("Bearer")){
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            return webClient.build()
                    .post() // metodo de la peticion
                    .uri("http://service-auth/api/v1/auth/validate?token="+ chunks[1]) // url del servicio de autenticacion
                    .retrieve() // enviar la peticion
                    .bodyToMono(TokenDto.class)
                    .map(t -> {
                        t.getToken();
                        return exchange;
                    }).flatMap(chain::filter);

        })));
    }

    public Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus){
        ServerHttpResponse res = exchange.getResponse();
        res.setStatusCode(httpStatus);
        return res.setComplete();
    }

    public static class Config{

    }

}
