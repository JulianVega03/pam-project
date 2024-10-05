package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.EstadoDocEntity;

import lombok.Data;

@Data
public class AspiranteEstadoDocResponse {
    private String nombre;

    private String apellido;

    private String correoPersonal;

    private String telefono;

    private EstadoDocEntity estado;
}
