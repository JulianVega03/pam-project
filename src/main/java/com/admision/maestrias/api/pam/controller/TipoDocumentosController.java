package com.admision.maestrias.api.pam.controller;

import com.admision.maestrias.api.pam.shared.dto.TipoDocumentoDTO;
import com.admision.maestrias.api.pam.service.implementations.TipoDocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RestController para los tipos de documentos
 * Está mapeado a la ruta "/tiposDoc"
 * @author Julian Camilo Riveros Fonseca
 */
@RestController
@RequestMapping(value = "/tiposDoc")
public class TipoDocumentosController {

    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    /**
     * Método GET para listar todos los tipos de documentos
     * @return ResponseEntity con el listado y estado OK
     */
    @GetMapping
    public ResponseEntity<List<TipoDocumentoDTO>> listar(){
        return new ResponseEntity<>(tipoDocumentoService.listar(), HttpStatus.OK);
    }
}
