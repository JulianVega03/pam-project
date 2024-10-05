package com.admision.maestrias.api.pam.models.responses;

import lombok.Data;

@Data
public class AdmitidosResponse {

    private Integer id;

    private String nombre;

    private String apellido;

    private Double puntaje_documentos;

    private Integer puntaje_entrevista;

    private Integer puntaje_prueba;

    private Double total_puntaje;
}
