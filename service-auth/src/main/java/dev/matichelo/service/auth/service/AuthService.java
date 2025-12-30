package dev.matichelo.service.auth.service;

import dev.matichelo.service.auth.dto.AuthUserDto;
import dev.matichelo.service.auth.dto.AuthUserRequest;
import dev.matichelo.service.auth.dto.RequestDto;
import dev.matichelo.service.auth.dto.TokenDto;
import dev.matichelo.service.auth.entity.AuthUser;
import dev.matichelo.service.auth.repository.AuthUserRepository;
import dev.matichelo.service.auth.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthUser save(AuthUserRequest authUserDto){
        Optional<AuthUser> userOptional = authUserRepository.findByUsername(authUserDto.getUsername());
        if(userOptional.isPresent()){
            return null; // Usuario ya existe
        }
        String password = passwordEncoder.encode(authUserDto.getPassword());
        AuthUser authUser= AuthUser.builder()
                .username(authUserDto.getUsername())
                .password(password)
                .role(authUserDto.getRole())
                .build();
        return authUserRepository.save(authUser);
    }

    public TokenDto login(AuthUserDto authUserDto){
        Optional<AuthUser> userOptional = authUserRepository.findByUsername(authUserDto.getUsername());
        if(userOptional.isEmpty()){
            return null;
        }
        if(passwordEncoder.matches(authUserDto.getPassword(), userOptional.get().getPassword())){
            return new TokenDto(jwtProvider.createToken(userOptional.get()));
        }
        return null;
    }

    public TokenDto validate(String token, RequestDto requestDto){
        if(!jwtProvider.validateToken(token, requestDto)){
            return null;
        }
        String username = jwtProvider.getUsernameFromToken(token);
        if(authUserRepository.findByUsername(username).isEmpty()){
            return null;
        }
        return new TokenDto(token);
    }


}
