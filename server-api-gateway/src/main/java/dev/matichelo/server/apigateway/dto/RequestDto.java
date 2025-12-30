package dev.matichelo.server.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RequestDto {
    private String uri; // uri es la ruta del endpoint al que se está accediendo
    private String method;
}
