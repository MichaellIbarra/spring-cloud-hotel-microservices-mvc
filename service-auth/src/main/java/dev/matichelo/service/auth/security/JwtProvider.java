package dev.matichelo.service.auth.security;

import dev.matichelo.service.auth.entity.AuthUser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct // Método que se ejecuta después de la construcción del bean
    protected void init(){
        // se necesita codificar el secret en Base64 porque la librería JWT lo requiere así ya que espera una cadena codificada en Base64 para la firma y verificación de tokens.
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(AuthUser authUser){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", authUser.getId());
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

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex){
            return false;
        }
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

}
