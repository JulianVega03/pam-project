package com.admision.maestrias.api.pam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase de entidad que representa la tabla "cohorte" en la base de datos.
 * @author Gibson Arbey, Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cohorte", uniqueConstraints= {@UniqueConstraint(columnNames= {"id"})})
public class CohorteEntity implements Serializable{

    private static final long serialVersionUID = 1L;
    
    /**
     * Identificador único de la entidad.
     * Se genera automáticamente mediante el uso de la estrategia de generación de identificación IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    /**
     * Fecha de inicio de la cohorte
     */
    @Column(name = "fecha_inicio",nullable = false)
    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private Date fechaInicio;

    /**
     * Fecha de finalización de la cohorte
     */
    @Column(name = "fecha_fin",nullable = false)
    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private Date fechaFin;
    
    /**
     * La cohorte esta habilitada 
     */
    @Column(name = "habilitado", columnDefinition = "BIT(1) default 0")
    private Boolean habilitado;

    /**
     * Enlace de ingreso a la entrevista
     */
    @Column(length = 255)
    private String enlace_entrevista;


    /**
     * Enlace para presentar la prueba
     */
    @Column(length = 255)
    private String enlace_prueba;

    /**
     * Fecha hasta la que va a estar habilitada la prueba
     */
    @Column(name = "fecha_max_prueba")
    private LocalDateTime fechaMaxPrueba;

    /**
     * Relación uno a muchos entre Cohorte y Aspirante
     */
    @JsonIgnore
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "cohorte")
    private List<AspiranteEntity> aspirantes = new ArrayList<>();

}