package com.admision.maestrias.api.pam.models.requests;

import com.admision.maestrias.api.pam.shared.dto.DocumentoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoRequest {
    /**
     * Anexar dos (2) fotos, tamaño 3x4 en un mismo documento. (Archivo JPG, PNG).
     */
    private DocumentoDTO fotos;
    /**
     * Anexar copia del documento de identidad por ambos lados
     */
    private DocumentoDTO di;
    /**
     * Anexar copia diploma de pregrado (Archivo en formato PDF):
     */
    private DocumentoDTO diplomaPregrado;
    /**
     * Anexar copia de las notas de pregrado
     * (Graduado de pregrado en la Universidad Francisco de Paula Santander:
     * no tendrá que hacer entrega del certificado de calificaciones según acuerdo 044 de 2016).
     * (Archivo formato PDF)
     */
    private DocumentoDTO notasPregrado;
    /**
     * Adjuntar acta de compromiso si es necesario. (Archivo en formato PDF)
     */
    private DocumentoDTO actaCompromiso;
    /**
     * Anexar resumen de la hoja de vida del aspirante
     */
    private DocumentoDTO cv;
    /**
     *Anexar comprobante de pago de derechos de inscripción (15% SMMLV). (Archivo en formato PDF).
     */
    private DocumentoDTO comprobantePago;
    /**
     * Anexar firma digitalizada
     */
    private DocumentoDTO firma;
    /**
     * Carta de referencia académica (1). (Archivo en formato PDF).
     */
    private DocumentoDTO carta1;
    /**
     * Carta de referencia académica (1). (Archivo en formato PDF).
     */
    private DocumentoDTO carta2;
    /**
     * Anexar Formato de Inscripción debidamente diligenciado
     */
    private DocumentoDTO formatoInscripcion;

}