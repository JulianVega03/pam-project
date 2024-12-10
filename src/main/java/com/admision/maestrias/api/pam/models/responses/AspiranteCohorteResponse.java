package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.EstadoEntity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

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
    /**
     * Puntaje de entrevista del aspirante
     */
    private Integer puntaje_entrevista;

    private boolean es_egresado_ufps;

    private String lugar_nac;

    private String genero;

    private Date fecha_exp_di;

    private Date fecha_nac;

    private String no_documento;

    private String departamento_residencia;

    private String municipio_residencia;

    private String direccion_residencia;

    private String empresa_trabajo;

    private String pais_trabajo;

    private String departamento_trabajo;

    private String municipio_trabajo;

    private String direccion_trabajo;

    private String estudios_pregrado;

    private String estudios_posgrados;

    private String exp_laboral;

    private Integer puntaje_prueba;

    private String documentType;

    private String estadoCivilTypes;

    private String zonaResidenciaTypes;

    private String grupoEtnicoTypes;

    private String puebloIndigenaTypes;

    private String otroPueblo;

    private String poseeDiscapacidadTypes;

    private String discapacidadTypes;

    private String capacidadxcepcionalTypes;

    private String tipoVinculacionTypes;

    private String promedioPregrado;

}
