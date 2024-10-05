package com.admision.maestrias.api.pam.models.requests;

import lombok.Data;

@Data
public class AspiranteDocumentoRequest {
    
    private Integer id;
    private Integer puntajeCartasReferencia;
    private Integer puntajeNotasPregrado;
    private Double puntajePublicaciones;
    private Double puntajeDistincionesAcad;
    private Double puntajeExperiencia;
    
}
