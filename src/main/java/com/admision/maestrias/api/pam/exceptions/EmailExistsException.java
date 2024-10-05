package com.admision.maestrias.api.pam.exceptions;

/**
 * Excepción lanzada si no encuentra un correo
 * @author Juan Pablo Correa Tarazona
 */
public class EmailExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public EmailExistsException(String message) {
        super(message);
    }

}
