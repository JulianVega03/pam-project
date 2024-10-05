package com.admision.maestrias.api.pam.models.requests;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class PruebaRequest {
    
    private String enlace_prueba;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd'T'HH:mm:ss")
    private LocalDateTime fechaMaxPrueba;
}
