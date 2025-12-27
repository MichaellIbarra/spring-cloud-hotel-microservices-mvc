package dev.matichelo.service.user.controller;

import dev.matichelo.service.user.entity.User;
import dev.matichelo.service.user.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user){
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    private int countRetry= 1;
    @GetMapping("/{id}")
//    @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback") // Nombre del circuito y el método de fallback
    @Retry(name = "ratingHotelRetry", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getUserById(@PathVariable String id){
        log.info("Listing user by id: {}", id);
        log.info("Retry count: {}", countRetry++);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<User> ratingHotelFallback( String id, Exception ex){
        log.info("Fallback is executed because service is down: {}", ex.getMessage());
        User user = User.builder()
                .id(id)
                .name("temp user")
                .email("temp@system.com")
                .info("This user is created because some service is down")
                .build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
