package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.EstadoEntity;

import lombok.Data;

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

    private boolean es_egresado_ufps;

    private String lugar_nac;
}
