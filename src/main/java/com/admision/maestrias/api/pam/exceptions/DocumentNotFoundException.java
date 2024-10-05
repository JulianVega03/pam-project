package com.admision.maestrias.api.pam.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un documento
 * @author Juan Pablo Correa Tarazona
 */
public class DocumentNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public DocumentNotFoundException (String message) { super(message); }
}
