package dev.matichelo.service.user.client;

import dev.matichelo.service.user.entity.Hotel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Definición del cliente Feign para el servicio de hotel
// tiene dos parametros: name: el nombre del servicio al que se va a conectar (service-hotel en este caso), y url: la URL base del servicio (opcional si se utiliza un servidor de descubrimiento).
@FeignClient(name = "${service.hotel.name}")
public interface HotelClient {

    @GetMapping("/api/v1/hotels")
    List<Hotel> getAllHotels();

    @GetMapping("/api/v1/hotels/{hotelId}")
    Hotel getHotelById(@PathVariable String hotelId);

}
