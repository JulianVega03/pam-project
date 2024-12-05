package com.admision.maestrias.api.pam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase de entidad que representa la tabla "aspirante" en la base de datos.
 * @author Gibson Arbey, Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "aspirante")
public class AspiranteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la entidad.
     * Se genera automáticamente mediante el uso de la estrategia de generación de
     * identificación IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Relación muchos a uno entre Aspirante y Cohorte
     */
     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "cohorte_id", nullable = false)
     @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
     private CohorteEntity cohorte;

    /**
     * Relación uno a uno entre Aspirante y User
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Nombre del usuario
     */
    @Column(nullable = false , length =  50)
    @NotEmpty
    private String nombre;

    /**
     * Apellido del aspirante
     */
    @Column(nullable = false , length =  50)
    @NotEmpty
    private String apellido;
    /**
     * Genero del aspirante
     */
    @Column(nullable = false, length = 20)
    @NotEmpty
    private String genero;

    /**
     * Lugar de nacimiento del aspirante
     */
    @Column(nullable = false, length = 50)
    @NotEmpty
    private String lugar_nac;

    /**
     * Fecha de expedición del documento de identidad del aspirante
     */
    @Column(nullable = false)
    private Date fecha_exp_di;

    /**
     * Fecha de nacimiento del aspirante
     */
    @Column(nullable = false)
    private Date fecha_nac;

    /**
     * Número del documento de identidad del aspirante
     */
    @Column(nullable = false, length = 20)
    @NotEmpty
    private String no_documento;

    /**
     * Correo personal del aspirante
     */
    @Column(name="correo_personal", nullable = false, length = 255)
    @NotEmpty
    @Email
    private String correoPersonal;

    /**
     * Departamento de residencia del aspirante
     */
    @Column(nullable = false, length = 50)
    @NotEmpty
    private String departamento_residencia;

    /**
     * Municipio de residencia del aspirante
     */
    @Column(length = 50)
    private String municipio_residencia;

    /**
     * Dirección de residencia del aspirante
     */
    @Column(length = 100)
    private String direccion_residencia;

    /**
     * Número de teléfono del aspirante
     */
    @Column(nullable = false, length = 30)
    @NotEmpty
    private String telefono;

    /**
     * Nombre de la empresa donde trabaja el aspirante
     */
    @Column(nullable = false, length = 100)
    @NotEmpty
    private String empresa_trabajo;

    /**
     * Pais donde trabaja el aspirante
     */
    @Column(length = 100)
    private String pais_trabajo;

    /**
     * Departamento donde trabaja el aspirante
     */
    @Column(nullable = false, length = 50)
    @NotEmpty
    private String departamento_trabajo;

    /**
     * Municipio donde trabaja el aspirante
     */
    @Column(nullable = false, length = 50)
    @NotEmpty
    private String municipio_trabajo;

    /**
     * Dirección donde trabaja el aspirante
     */
    @Column(nullable = false, length = 100)
    @NotEmpty
    private String direccion_trabajo;

    /**
     * Estudios a nivel de formación universitaria (pre-grado)
     */
    @Column(nullable = false)
    @NotEmpty
    private String estudios_pregrado;

    /**
     * Estudios a nivel de formación universitaria (pos-grado)
     */
    @Column
    private String estudios_posgrados;

    /**
     * Información de la Experiencia Laboral
     */
    @Column(nullable = false)
    @NotEmpty
    private String exp_laboral;

    /**
     * Es egresado de la UFPS
     */
    @Column(nullable = false)
    private Boolean es_egresado_ufps;

    /**
     * Puntaje total (suma de los puntajes de documentos, entrevista y prueba)
     */
    @Transient
    private Double total;

    private Integer puntajeNotas;

    private Double puntajeDistincionesAcademicas;

    private Double puntajeExperienciaLaboral;
    
    private Double puntajePublicaciones;

    private Integer puntajeCartasReferencia;
    /**
     * Puntaje del aspirante en los documentos
     */
    @Transient
    private Double puntajeDocumentos;

    /**
     * Puntaje del aspirante en la entrevista
     */
    private Integer puntaje_entrevista;

    /**
     * Puntaje del aspirante en la prueba
     */
    private Integer puntaje_prueba;

    /**
     * Fecha y hora para presentar la entrevista
     */
    private LocalDateTime fecha_entrevista;

    /**
     * Enlace de la sala para presentar la entrevista
     */
    private String enlace_entrevista;

    /**
     * Sala para presentar la entrevista
     */
    private String sala_entrevista;

    /**
     * Relación muchos a uno entre Aspirante y Estado
     */
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoEntity estado;

    /**
     * Relación uno a muchos entre Aspirante y Documentos
     */
    @JsonIgnore
    @OneToMany(mappedBy = "aspirante",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoEntity> documentos = new ArrayList<>();

    /**
     * Relación muchos a uno entre Aspirante y Notificación
     */
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "aspirante", orphanRemoval = true)
    private List<NotificacionEntity> notificaciones = new ArrayList<>();

    /**
     * Calcula el puntaje total del aspirante
     * @return Entero con la sumatoria final
     */
    public Double getTotal() {
        if(getPuntajeDocumentos()== null) setPuntajeDocumentos(0d);
        if(getPuntaje_entrevista()== null) setPuntaje_entrevista(0);
        if(getPuntaje_prueba()== null) setPuntaje_prueba(0);
        
        return getPuntajeDocumentos() + getPuntaje_entrevista() + getPuntaje_prueba();
    }
    
    public Double getPuntajeDocumentos(){
        if(getPuntajeCartasReferencia()== null) setPuntajeCartasReferencia(0);
        if(getPuntajeNotas()==null ) setPuntajeNotas(0);
        if(getPuntajeDistincionesAcademicas() == null) setPuntajeDistincionesAcademicas(0d);
        if(getPuntajeExperienciaLaboral()==null) setPuntajeExperienciaLaboral(0d);
        if(getPuntajePublicaciones() == null) setPuntajePublicaciones(0d);
        
        return  getPuntajeDistincionesAcademicas() + getPuntajeExperienciaLaboral() + getPuntajePublicaciones() + getPuntajeCartasReferencia() + getPuntajeNotas();
    }
    
}
