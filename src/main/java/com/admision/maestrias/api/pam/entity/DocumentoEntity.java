package com.admision.maestrias.api.pam.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Clase de entidad que representa la tabla "documento" en la base de datos.
 * @author Gibson Arbey, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documento")
public class DocumentoEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "aspirante_id", nullable = false, updatable = false)
    private AspiranteEntity aspirante;

    @ManyToOne
    @JoinColumn(name = "documento_id", nullable = false, updatable = false)
    private TipoDocumentoEntity documento;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoDocEntity estado;

    /**
     * url donde se encuentra el documento 
     */
    @Column(nullable = true)
    @NotEmpty
    private String url;

    /**
     * Key del archivo en el s3
     */
    @Column(nullable = false)
    private String keyFile;

    /**
     * Extensión del archivo subido
     */
    @Column(nullable = false)
    private String formato;

    /**
     * Retroalimentacion sobre documentos rechazados
     */
    @Column(nullable = true)
    private String retroalimentacion;


}
