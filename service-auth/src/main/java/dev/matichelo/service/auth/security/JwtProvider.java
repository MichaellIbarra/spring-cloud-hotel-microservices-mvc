package dev.matichelo.service.auth.security;

import dev.matichelo.service.auth.dto.RequestDto;
import dev.matichelo.service.auth.dto.enums.Role;
import dev.matichelo.service.auth.entity.AuthUser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    private final RouteValidator routeValidator;

    @PostConstruct // Método que se ejecuta después de la construcción del bean
    protected void init(){
        // se necesita codificar el secret en Base64 porque la librería JWT lo requiere así ya que espera una cadena codificada en Base64 para la firma y verificación de tokens.
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(AuthUser authUser){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", authUser.getId());
        claims.put("role", authUser.getRole());
        claims.put("username", authUser.getUsername());

        Date now = new Date();
        Date exp = new Date(now.getTime() + 3600000); // 1 hora de validez

        return Jwts.builder()
                .claims(claims)
                .subject(authUser.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

    }

    public boolean validateToken(String token, RequestDto requestDto){
        try{
            Jwts.parser()
                    .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception ex){
            return false;
        }
        // verificar si la ruta está protegida
        if(!routeValidator.isProtected(requestDto)){
            return true; // la ruta no está protegida, no es necesario verificar roles
        }

        List<Role> requiredRoles = routeValidator.getRequiredRoles(requestDto);

        if(requiredRoles.isEmpty()){
            return true; // no se requieren roles específicos para esta ruta
        }
        // verificar si el usuario tiene alguno de los roles requeridos
        Role userRole = getRoleFromToken(token);
        return requiredRoles.contains(userRole);

    }

    public String getUsernameFromToken(String token){
        try{
            return Jwts.parser()
                    .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return "bad token";
        }
    }
    private Role getRoleFromToken(String token){
     try{
         String roleStr = Jwts.parser()
                 .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                 .build()
                 .parseSignedClaims(token)
                 .getPayload()
                 .get("role", String.class);
         return Role.valueOf(roleStr);
     } catch (Exception e) {
         return null;
     }
    }

}
