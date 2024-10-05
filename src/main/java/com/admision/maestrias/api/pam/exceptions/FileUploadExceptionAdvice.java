package com.admision.maestrias.api.pam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Clase que proporciona manejo de excepciones para la subida de archivos.
 * @author Julian Camilo Riveros Fonseca
 */
@ControllerAdvice
public class FileUploadExceptionAdvice {
    /**
     * Maneja una excepción cuando se excede el tamaño máximo de carga de archivos.
     * @param ex La excepción de MaxUploadSizeExceededException.
     * @return ResponseEntity con un mensaje de error y el estado HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Los archivos no pueden exceder un tamaño de 5MB");
    }

}
