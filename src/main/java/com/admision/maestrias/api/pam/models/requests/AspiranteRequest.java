package com.admision.maestrias.api.pam.models.requests;

import java.util.Date;

import lombok.Data;

@Data
public class AspiranteRequest {

    private String nombre;

    private String apellido;
    
    private String genero;

    private String lugar_nac;

    private Date fecha_exp_di;

    private Date fecha_nac;

    private String no_documento;

    private String departamento_residencia;

    private String municipio_residencia;

    private String direccion_residencia;

    private String telefono;

    private String empresa_trabajo;

    private String departamento_trabajo;

    private String municipio_trabajo;

    private String direccion_trabajo;

    private String estudios_pregrado;

    private String estudios_posgrados;

    private String exp_laboral;

    private Boolean es_egresado_ufps;
}
