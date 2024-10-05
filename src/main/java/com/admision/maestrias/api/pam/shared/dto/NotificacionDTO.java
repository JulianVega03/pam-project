package com.admision.maestrias.api.pam.shared.dto;

import java.util.Date;

import lombok.Data;

@Data
public class NotificacionDTO {
     
    private Integer id;
    private String enunciado;
    private Boolean estado;
    private Date fecha_envio;
    private int idAspirante;
}
