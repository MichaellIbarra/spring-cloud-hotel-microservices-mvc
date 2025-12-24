package dev.matichelo.service.hotel.service.impl;

import dev.matichelo.service.hotel.entity.Hotel;
import dev.matichelo.service.hotel.exception.ResourceNotFoundException;
import dev.matichelo.service.hotel.repository.HotelRepository;
import dev.matichelo.service.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Override
    public Hotel saveHotel(Hotel hotel) {
        return  hotelRepository.save(hotel);
    }

    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel getHotelById(String id) {
        return hotelRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Hotel not found with " +
                "id: " + id));
    }
}
