package com.admision.maestrias.api.pam.models.responses;

import com.admision.maestrias.api.pam.entity.EstadoDocEntity;
import com.admision.maestrias.api.pam.entity.TipoDocumentoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoResponse {
    /**
     * Estado del documento(rechazado, aprobado, en espera..)
     */
    private EstadoDocEntity estado;
    /**
     * tipo de documento(foto, cv, notas de pregrado...)
     */
    private TipoDocumentoEntity documento;
    /**
     *
     */
    private String keyFile;
    private String formato;
    private String url;
}
