package dev.matichelo.service.hotel.service;

import dev.matichelo.service.hotel.entity.Hotel;

import java.util.List;

public interface HotelService {
    Hotel saveHotel(Hotel hotel);
    List<Hotel> getAllHotels();
    Hotel getHotelById(String id);
}
