package com.admision.maestrias.api.pam.shared.dto;

import com.admision.maestrias.api.pam.entity.EstadoEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AspiranteDTO {
    private Integer id;
  
    private String nombre;

    private String apellido;

    private String genero;

    private String lugar_nac;

    private Date fecha_exp_di;

    private Date fecha_nac;

    private String no_documento;

    private String correoPersonal;

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
    
    private Integer puntajeNotas;

    private Integer puntajeHojaDeVida;

    private Integer puntajeCartasReferencia;
    
    private Double puntajeDistincionesAcademicas;

    private Double puntajeExperienciaLaboral;
    
    private Double puntajePublicaciones;

    private Double puntajeDocumentos;

    private Integer puntaje_entrevista;

    private Integer puntaje_prueba;

    private Double total;

    private LocalDateTime fecha_entrevista;

    private String enlace_entrevista;

    private String sala_entrevista;

    private EstadoEntity estado;

    private String pais_trabajo;

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



    @Override
    public String toString() {
        return id +" - " + nombre + " " + apellido;

    }
}
