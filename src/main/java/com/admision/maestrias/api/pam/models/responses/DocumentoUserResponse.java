package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.EstadoDocEntity;

import lombok.Data;

@Data
public class DocumentoUserResponse {
    
    private String nombre;
    
    private EstadoDocEntity estado;

    private String url_formato;

    private int idDocumento;

}
