package com.admision.maestrias.api.pam.models.responses;

import java.util.Date;

import lombok.Data;

@Data
public class NotificacionResponse {

   
    private String enunciado;

   
    private Boolean estado;

  
    private Date fecha_envio;

    
}
