package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.EstadoEntity;

import lombok.Data;

@Data
public class CohorteAspirantesResponse {
    private Integer id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correoPersonal;
    private EstadoEntity estado;

}
