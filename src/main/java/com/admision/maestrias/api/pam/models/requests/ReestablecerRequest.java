package com.admision.maestrias.api.pam.models.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ReestablecerRequest {
    
    private String actualContraseña;

    @NotEmpty(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 30, message = "La contraseña debe tener entre 8 y 30 caracteres")
    private String nuevaContraseña;
}