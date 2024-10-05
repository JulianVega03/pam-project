package com.admision.maestrias.api.pam.shared.dto;

import lombok.Data;

@Data
public class StateDTO {
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
     * 5."ENTREVISTA",
     * 6."PRUEBA",
     * 7."ADMITIDO",
     * 8."EN ESPERA",
     * 9."DESERTO",
     * 10."DESACTIVADO"
     */
    private String description;

}
