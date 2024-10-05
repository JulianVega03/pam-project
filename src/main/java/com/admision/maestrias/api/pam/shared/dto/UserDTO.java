package com.admision.maestrias.api.pam.shared.dto;

import lombok.Data;

@Data
public class UserDTO {

    /**
     * El ID del usuario.
     */
    private Integer id;

    /**
     * El correo electrónico del usuario.
     */
    private String email;

    /**
     * La contraseña del usuario (sin encriptar).
     */
    private String password;

    /**
     * La contraseña del usuario encriptada.
     */
    private String encryptedPassword;

}
