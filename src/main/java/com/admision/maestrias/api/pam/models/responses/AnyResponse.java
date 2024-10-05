package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.DocumentoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnyResponse {
    private String message;

    public AnyResponse(int value, String documentoAprobado, DocumentoEntity documentorechazar) {
    }
}
