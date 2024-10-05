package com.admision.maestrias.api.pam.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * Clase de entidad que representa la tabla "notificacion" en la base de datos.
 * @author Gibson Arbey, Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificacion")
public class NotificacionEntity implements Serializable {

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
     * Relación muchos a uno entre Notificación y Aspirante
     */
    @ManyToOne
    @JoinColumn(name = "aspirante_id", nullable = false)
    private AspiranteEntity aspirante;

    /**
     * Enunciado de la notificación
     */
    @Column(nullable = false)
    @NotEmpty
    private String enunciado;

    /**
     * Estado de la notificación
     */
    @Column(nullable = false)
    private Boolean estado;

    /**
     * Fecha de envío de la notificación
     */
    @Column(nullable = false)
    private Date fecha_envio;

}
