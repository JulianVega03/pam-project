    package com.admision.maestrias.api.pam.shared.dto;

    import lombok.Builder;

    import lombok.Value;


    import java.net.URL;

    /**
     * DTO de DocumentoEntity
     */
    @Value
    @Builder
    public class DocumentoDTO {
        /**
         * Clave del archivo en el s3
         */
        private String keyFile;
        /**
         * Url del archivo en el s3
         */
        private URL url;
        /**
         * extensión del archivo
         */
        private String formato;
        /**
         * Tipo de documento
         */
        private TipoDocumentoDTO tipoDocumentoDTO;
        /**
         * Retroalimentación del documento
         */
        private String retroalimentacion;

    }