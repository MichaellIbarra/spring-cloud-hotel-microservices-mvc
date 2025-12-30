package dev.matichelo.service.auth.controller;

import dev.matichelo.service.auth.dto.AuthUserDto;
import dev.matichelo.service.auth.dto.AuthUserRequest;
import dev.matichelo.service.auth.dto.RequestDto;
import dev.matichelo.service.auth.dto.TokenDto;
import dev.matichelo.service.auth.entity.AuthUser;
import dev.matichelo.service.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login (@RequestBody AuthUserDto authUserDto){
        TokenDto tokenDto = authService.login(authUserDto);
        if(tokenDto == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenDto> validate(@RequestParam String token, @RequestBody RequestDto requestDto){
        TokenDto tokenDto = authService.validate(token, requestDto);
        if(tokenDto == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthUser> register(@RequestBody AuthUserRequest authUserDto){
        AuthUser authUser = authService.save(authUserDto);
        if(authUser == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authUser);
    }

}
