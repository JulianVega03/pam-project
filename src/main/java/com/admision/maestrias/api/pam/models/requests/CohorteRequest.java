package com.admision.maestrias.api.pam.models.requests;

import java.util.Date;

import lombok.Data;

@Data
public class CohorteRequest {
    
    private Date fechaInicio;

    private Date fechaFin;
}
