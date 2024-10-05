package com.admision.maestrias.api.pam.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoDocumentoDTO {
    /**
     * Descripción del documento que se debe subir
     */
    private String nombre;
    /**
     * URL del formato de un documento (si lo tiene).
     */
    private String url_formato;
}
