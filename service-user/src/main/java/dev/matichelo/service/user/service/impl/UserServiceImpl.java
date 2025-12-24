package dev.matichelo.service.user.service.impl;

import dev.matichelo.service.user.entity.Grade;
import dev.matichelo.service.user.entity.Hotel;
import dev.matichelo.service.user.entity.User;
import dev.matichelo.service.user.exception.ResourceNotFoundException;
import dev.matichelo.service.user.repository.UserRepository;
import dev.matichelo.service.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // getLogger es un método estático de la clase LoggerFactory que se utiliza para obtener una instancia del registrador (logger) asociado a una clase específica.
    // Esto permite que los mensajes de registro se asocien con la clase desde la cual se están registrando, lo que facilita la identificación del origen de los mensajes en los registros.

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;


    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("User not found with" +
            " id: "+ id));

        Grade[]  gradesByUser =
                restTemplate.getForObject("http://msvc-grades/api/v1/grades/users/"+ user.getId(), Grade[].class);
        List<Grade> gradesList = Arrays.stream(gradesByUser).toList();


        List<Grade> gradesWithHotel = gradesList.stream().map(grade -> {
            logger.info("Grade Id: {}", grade.getId());
            // Llamada al servicio de hoteles para obtener los detalles del hotel asociado a la calificación
            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://msvc-hotel/api/v1/hotels/"+ grade.getHotelId(), Hotel.class);
            Hotel hotel = forEntity.getBody();
            grade.setHotel(hotel);
            return grade;
        }).toList();
        logger.info("{}", gradesWithHotel);
        user.setGrades(gradesWithHotel);
        return user;
    }
}
