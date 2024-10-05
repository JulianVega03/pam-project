package com.admision.maestrias.api.pam.models.responses;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EntrevistaPruebaResponse {
    
    private LocalDateTime fechaMaxPrueba;

    private String enlace_prueba;

    private String enlace_entrevista;

    private LocalDateTime fecha_entrevista;
}
