package com.admision.maestrias.api.pam.shared.dto;

import lombok.Data;

@Data
public class EstadoDTO {
    /**
     * Identificador unico del estado
     */
    private Integer id;

    /**
     * Descripcion del estado del aspirante el cual puede ser:
     * 1."INSCRIPCION",
     * 2."ENVIO DOCUMENTOS",
     * 3."DOCUMENTOS ENVIADOS",
     * 4."DOCUMENTOS APROBADOS",
     * 5."ENTREVISTA Y PRUEBA",
     * 6."ADMITIDO",
     * 7."EN ESPERA",
     * 8."DESERTO",
     * 9."DESACTIVADO"
     */
    private String description;

}
