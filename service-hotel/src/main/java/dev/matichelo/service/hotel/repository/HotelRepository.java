package dev.matichelo.service.hotel.repository;

import dev.matichelo.service.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, String> {
}
