package com.admision.maestrias.api.pam.models.responses;

import lombok.Data;

@Data
public class CalificacionesResponse {
    
    private String nombre;
    private String apellido;
    private Double puntajeDocumentos;
    private Integer puntaje_entrevista;
    private Integer puntaje_prueba;
    private Double total;
}
