package dev.matichelo.service.user.service.impl;

import dev.matichelo.service.user.client.GradeClient;
import dev.matichelo.service.user.client.HotelClient;
import dev.matichelo.service.user.entity.Grade;
import dev.matichelo.service.user.entity.Hotel;
import dev.matichelo.service.user.entity.User;
import dev.matichelo.service.user.exception.ResourceNotFoundException;
import dev.matichelo.service.user.repository.UserRepository;
import dev.matichelo.service.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // getLogger es un método estático de la clase LoggerFactory que se utiliza para obtener una instancia del registrador (logger) asociado a una clase específica.
    // Esto permite que los mensajes de registro se asocien con la clase desde la cual se están registrando, lo que facilita la identificación del origen de los mensajes en los registros.

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final HotelClient hotelClient;
    private final GradeClient gradeClient;


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

//        List<Grade> gradesList = Arrays.stream(
//                restTemplate.getForObject("http://service-grade/api/v1/grades/users/"+ user.getId(), Grade[].class)
//        ).toList();
        List<Grade> gradesList = gradeClient.getGradesByUserId(user.getId());

//        List<Hotel> hotels = Arrays.stream(
//                restTemplate.getForObject("http://service-hotel/api/v1/hotels", Hotel[].class)
//        ).toList();
        List<Hotel> hotels = hotelClient.getAllHotels();

        Map<String, Hotel> hotelMap = hotels.stream()
                // filter elimina los elementos del stream que no cumplen con la condición especificada en el predicado.
                .filter(h -> h.getId() != null)
                // collect se utiliza para acumular los elementos de un stream en una colección u otro tipo de resultado.
                // toMap es un método estático de la clase Collectors que se utiliza para crear un colector que acumula los elementos de un stream en un mapa (Map).
                // En este caso, toMap toma dos funciones como argumentos: la primera función (Hotel::getId) se utiliza para obtener la clave del mapa (el ID del hotel),
                // y la segunda función (Function.identity()) se utiliza para obtener el valor del mapa (el objeto Hotel en sí).
                .collect(Collectors.toMap(Hotel::getId, Function.identity()));
        logger.info(hotelMap.toString());
        List<Grade> gradeWithHotel  =  gradesList.stream().map(grade -> {
            logger.info("Grade Id: {}", grade.getId());
            // Llama el servicio de Hotel cada vez que itera
//            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://msvc-hotel/api/v1/hotels/"+ grade.getHotelId(), Hotel.class);
//            Hotel hotel = forEntity.getBody();

            // Usando la lista de hoteles obtenida previamente pero genera muchas iteraciones BIG O(N*M)
//            Hotel hotel = hotels.stream()
//                    .filter(h -> h.getId().equals(grade.getHotelId()))
//                    .findFirst()
//                    .orElse(null);
            // Usando el mapa de hoteles para obtener el hotel correspondiente en tiempo constante O(1)
            Hotel hotel = hotelMap.get(grade.getHotelId());

            grade.setHotel(hotel);

            return grade;
        }).toList();
        logger.info("{}", gradeWithHotel);
        user.setGrades(gradeWithHotel);
        return user;
    }
}
