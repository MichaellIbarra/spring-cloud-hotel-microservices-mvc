package dev.matichelo.service.auth.security;

import dev.matichelo.service.auth.dto.RequestDto;
import dev.matichelo.service.auth.dto.enums.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "protected-paths")
public class RouteValidator {

    private List<PathConfig> paths;

    // Retorna los roles requeridos para acceder a una ruta específica
    public List<Role> getRequiredRoles(RequestDto requestDto){
        return paths.stream()
                .filter(p -> Pattern.matches(p.getUri(), requestDto.getUri()) && p.getMethods().contains(requestDto.getMethod()))
                .findFirst()
                .map(PathConfig::getRoles)
                .orElse(List.of());
    }

    // Verifica si una ruta específica está protegida
    public boolean isProtected(RequestDto requestDto){
        return paths.stream()
                .anyMatch(p -> Pattern.matches(p.getUri(), requestDto.getUri()) && p.getMethods().contains(requestDto.getMethod()));
    }


    @Data
    public static class PathConfig {
        private String uri;
        private List<String> methods;
        private List<Role> roles;
    }

}
