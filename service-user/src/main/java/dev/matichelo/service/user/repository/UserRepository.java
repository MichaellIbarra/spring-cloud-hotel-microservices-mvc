package dev.matichelo.service.user.repository;

import dev.matichelo.service.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
// JpaRepository proporciona métodos CRUD para la entidad User
public interface UserRepository extends JpaRepository<User, String> {
}
