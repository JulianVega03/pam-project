package com.admision.maestrias.api.pam.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Clase de entidad que representa la tabla "users" en la base de datos.
 * @author Gibson Arbey, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@Table(indexes = {@Index(columnList = "email", name = "index_email", unique = true) })
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Identificador único de la entidad.
     * Se genera automáticamente mediante el uso de la estrategia de generación de identificación IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Email del usuario
     */
    @Column(nullable = false, length = 255)
    @NotEmpty
    @Email
    private String email;

    /**
     * Contraseña encriptada del usuario
     */
    @Column(name = "encrypted_password",nullable = false, length = 255)
    @NotEmpty
    private String encryptedPassword;

    @Column(name = "correo_confirmado", nullable = false, columnDefinition = "boolean default false")
    private boolean correoConfirmado;

    /**
     * Relación muchos a uno entre User y Rol
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "authority_id", nullable = false)
    private RolEntity rol;

    /**
     * Relación uno a uno entre User y Aspirante
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AspiranteEntity aspirante;

}