package com.admision.maestrias.api.pam.models.requests;

import lombok.Data;

@Data
public class AspiranteCohorteRequest {
    private String nombre;

    private String apellido;

    private String correoPersonal;

    private String telefono;
 
}
