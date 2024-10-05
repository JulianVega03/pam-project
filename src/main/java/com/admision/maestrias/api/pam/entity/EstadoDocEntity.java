package com.admision.maestrias.api.pam.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Clase de entidad que representa la tabla "estado_doc" en la base de datos.
 * @author Gibson Arbey, Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estado_doc")
public class EstadoDocEntity implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Identificador único de la entidad.
     * Se genera automáticamente mediante el uso de la estrategia de generación de identificación IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nombre del estado
     */
    @Column(nullable = false, length = 20)
    @NotEmpty
    private String nombre;

}
