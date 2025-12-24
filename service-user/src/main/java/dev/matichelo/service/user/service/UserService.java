package dev.matichelo.service.user.service;

import dev.matichelo.service.user.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
    User getUserById(String id);
}
