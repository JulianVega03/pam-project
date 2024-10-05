package com.admision.maestrias.api.pam.models.requests;

import lombok.Data;

@Data
public class RetroAlimentacionRequest {

    private String retroalimentacion;
    private Integer docId;
    private Integer aspiranteId;


}
