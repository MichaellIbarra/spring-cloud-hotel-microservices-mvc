package dev.matichelo.service.auth.dto;

import dev.matichelo.service.auth.dto.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AuthUserRequest {
    private String username;
    private String password;
    private Role role;
}
