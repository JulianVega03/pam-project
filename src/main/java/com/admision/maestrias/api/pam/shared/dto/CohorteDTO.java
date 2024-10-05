package com.admision.maestrias.api.pam.shared.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class CohorteDTO {
    private Integer id;
    private Date fechaInicio;
    private Date fechaFin;
    private boolean habilitado;
    private String enlace_entrevista;
    private String enlace_prueba;
    private LocalDateTime fechaMaxPrueba;
}

