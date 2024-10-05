package com.admision.maestrias.api.pam.shared.dto;

import lombok.Data;

import java.net.URL;

@Data
public class DocumentoReDTO {

    /**
     * Nombre del archivo que le day el usuario
     */
    String name;
    String key;
    URL url;
    String retroalimentacion;
    TipoDocumentoDTO tipoDocumentoDTO;

}
