package com.admision.maestrias.api.pam.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase de entidad que representa la tabla "estados" en la base de datos.
 * @author Gibson Arbey, Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estado")
public class EstadoEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la entidad.
     * Se genera automáticamente mediante el uso de la estrategia de generación de identificación IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Descrpcion del estado
     */
    @Column(length = 50)
    @NotEmpty
    private String descripcion;

    /**
     * Relacion uno a muchos entre Estado y Aspirante
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,  cascade = CascadeType.ALL, mappedBy = "estado")
    private List<AspiranteEntity> aspirante = new ArrayList<>();

}