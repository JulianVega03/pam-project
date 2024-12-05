package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.EstadoEntity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AspiranteCohorteResponse {
    /**
     * Id del aspirante
     */
    private int id;
    /**
     * Nombre del aspirante
     */
    private String nombre;
    /**
     * apellido del aspirante
     */
    private String apellido;
    /**
     * Correo personal del aspirante
     */
    private String correoPersonal;
    /**
     * telefono del aspirante
     */
    private String telefono;
    /**
     * Fase del proceso en la que se encuentra el aspirante(estado del aspirante)
     */
    private EstadoEntity estado;
    /**
     * Sala de entrevista del aspirante
     */
    private String sala_entrevista;
    /**
     * Fecha de entrevista del aspirante
     */
    private LocalDateTime fecha_entrevista;

    private boolean es_egresado_ufps;

    private String lugar_nac;
}
