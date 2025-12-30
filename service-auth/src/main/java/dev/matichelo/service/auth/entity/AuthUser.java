package dev.matichelo.service.auth.entity;

import dev.matichelo.service.auth.dto.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "auth_user")
@Entity
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
     @Enumerated(EnumType.STRING)
    private Role role;
}
