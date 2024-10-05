package com.admision.maestrias.api.pam.exceptions;

/**
 * Excepxión que controla si un cohorte existe
 * @author Juan Pablo Correa Tarazona
 */
public class CohorteExistException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public CohorteExistException (String mensaje) { super(mensaje); }
}
