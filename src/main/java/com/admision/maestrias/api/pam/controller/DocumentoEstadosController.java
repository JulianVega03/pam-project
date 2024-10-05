package com.admision.maestrias.api.pam.controller;

import com.admision.maestrias.api.pam.entity.DocumentoEntity;
import com.admision.maestrias.api.pam.entity.TipoDocumentoEntity;
import com.admision.maestrias.api.pam.models.requests.RetroAlimentacionRequest;
import com.admision.maestrias.api.pam.models.responses.AnyResponse;
import com.admision.maestrias.api.pam.models.responses.AspiranteEstadoDocResponse;
import com.admision.maestrias.api.pam.models.responses.DocumentoResponse;
import com.admision.maestrias.api.pam.models.responses.DocumentoUserResponse;
import com.admision.maestrias.api.pam.repository.TipoDocumentoRepository;
import com.admision.maestrias.api.pam.service.implementations.DocumentoService;
import com.admision.maestrias.api.pam.service.implementations.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona, Ingrid FLorez, Miguel Lara
 */
@RestController
@RequestMapping("/doc")
public class DocumentoEstadosController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private TipoDocumentoRepository documentoRepository;

    /**
     * Este método se encarga de aprobar un documento en la base de datos mediante
     * su ID y email asociados.
     * Solo accesible por usuarios con los roles administrador y encargado.
     * 
     * @param documentoId el ID del documento a aprobar.
     * @param aspiranteId el ID asociado al aspirante
     * @return Un objeto AnyResponse que contiene un mensaje indicando el resultado
     *         de la operación.
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @PutMapping("/aprobar/{documentoId}/{aspiranteId}")
    public ResponseEntity<AnyResponse> aprobarDocumento(@PathVariable Integer documentoId, @PathVariable Integer aspiranteId) {
        documentoService.cambiarEstadoDocumento(aspiranteId, documentoId, 4);
        return ResponseEntity.ok(new AnyResponse("Documento aprobado con exito"));
    }

    /**
     * Este método se encarga de rechazar un documento en la base de datos mediante
     * su ID y email asociados.
     * Solo accesible por usuarios con los roles administrador y encargado.
     *
     * @param documentoId el ID del documento a rechazar.
     * @param aspiranteId el ID asociado al aspirante
     * @return Un objeto AnyResponse que contiene un mensaje indicando el resultado
     *         de la operación.
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @PutMapping("/rechazar/{documentoId}/{aspiranteId}")
    public ResponseEntity<AnyResponse> rechazarDocumento(@PathVariable Integer documentoId, @PathVariable Integer aspiranteId) {
        documentoService.cambiarEstadoDocumento(aspiranteId, documentoId, 3);
        return ResponseEntity.ok(new AnyResponse("Documento rechazado con exito"));
    }

    /**
     * Este método lista los documentos asociados a un aspirante.
     * @return Una lista de objetos DocumentoUserResponse que representa los
     *         documentos del aspirante.
     */
    @Secured("ROLE_USUARIO")
    @GetMapping("/listar")
    public List<DocumentoUserResponse> listarDocumentosPorAspirante() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        try {
            List<DocumentoEntity> documentos = documentoService.listarDocumentosPorAspirante(email);
            List<DocumentoUserResponse> documentoResponses = new ArrayList<>();

            if (documentos != null) {
                for (DocumentoEntity documento : documentos) {
                    DocumentoUserResponse documentoResponse = new DocumentoUserResponse();
                    documentoResponse.setNombre(documento.getDocumento().getNombre());
                    documentoResponse.setEstado(documento.getEstado());
                    documentoResponse.setUrl_formato(documento.getDocumento().getUrl_formato());
                    documentoResponse.setIdDocumento(documento.getDocumento().getId());
                    documentoResponses.add(documentoResponse);
                }
            }
            return documentoResponses;
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No se encontró el aspirante con el id de la cuenta proporcionado.");
        }

    }

    /**
     * Este método lista los documentos asociados a un aspirante.
     * muestra toda la información de los documentos
     * Solo accesible por usuarios con los roles administrador y encargado.
     *
     * @param aspiranteId el ID del aspirante.
     * @return Una lista de objetos DocumentoResponse que representa los documentos
     *         del aspirante.
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @GetMapping("/listarDoc")
    public List<DocumentoResponse> listarDocumentosdeAspirante(@RequestParam("aspiranteId") Integer aspiranteId) {
        List<DocumentoEntity> documentos = documentoService.listarDocumentosDeAspirante(aspiranteId);
        return documentos.stream()
                .map(this::mapDocumentoEntityAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Endpoint enviar retroalimentación relacionada con un documento.
     * Solo accesible por usuarios con los roles administrador y encargado.
     *
     * @param retroalimentacionRequest contiene los datos de la retroalimentación
     * @return Un mensaje que indica si la retroalimentación fue exitosa
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @PostMapping("/retroalimentacion")
    public ResponseEntity<AnyResponse> rechazaryEnviarRetroalimentacion(
            @RequestBody @Valid RetroAlimentacionRequest retroalimentacionRequest) {

        documentoService.EnviarRetroalimentacion(retroalimentacionRequest.getAspiranteId(), retroalimentacionRequest.getDocId(),
                retroalimentacionRequest.getRetroalimentacion());

        TipoDocumentoEntity tipoDocumentoEntity = documentoRepository.findById(retroalimentacionRequest.getDocId()).get();

        notificacionService.crearNotificacion("El documento: " + tipoDocumentoEntity.getNombre() + " ha sido rechazado"
                        +" Razon: " + retroalimentacionRequest.getRetroalimentacion(),
                retroalimentacionRequest.getAspiranteId());

        return ResponseEntity.ok(new AnyResponse("Documento rechazado y enviada su retroalimentación"));
    }

    /**
     * Este método se encarga de listar los aspirantes que tienen un estado de
     * documento específico.
     * Solo está permitido para los roles de administrador y encargado.
     *
     * @param idEstado el ID del estado del documento.
     * @return Una lista de objetos AspiranteEstadoDocResponse que representan los
     *         aspirantes con el estado de documento dado.
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @GetMapping("/filtrar")
    public List<AspiranteEstadoDocResponse> listarAspirantesConEstado(@RequestParam Integer idEstado) {
        return documentoService.listarAspirantesConEstadoDoc(idEstado);
    }

    /**
     * Este método se encarga de crear los documentos para un aspirante específico y
     * devuelve una respuesta genérica.
     * Requiere que el usuario tenga el rol "ROLE_USUARIO".
     *
     * @param aspiranteId el ID del aspirante para el cual se crearán los
     *                    documentos.
     * @return Un objeto AnyResponse que contiene un mensaje indicando si los
     *         documentos se crearon exitosamente o no.
     */
    @Secured("ROLE_USUARIO")
    @PostMapping("/crearDocs")
    public ResponseEntity<AnyResponse> crearDocumentos(@RequestParam Integer aspiranteId) {
        List<DocumentoEntity> documentos = documentoService.crearDocumentos(aspiranteId);
        AnyResponse response = (!documentos.isEmpty()) ? new AnyResponse("Documentos creados exitosamente")
                : new AnyResponse("No se pudieron crear los documentos.");
        return ResponseEntity.ok(response);
    }

    /**
     * Mapea un documento entity a un DocumentoResponse
     * 
     * @param documento documento entity
     * @return DocumentoResponse
     */
    private DocumentoResponse mapDocumentoEntityAResponse(DocumentoEntity documento) {
        DocumentoResponse documentoResponse = new DocumentoResponse();
        documentoResponse.setEstado(documento.getEstado());
        documentoResponse.setUrl(documento.getUrl());
        documentoResponse.setKeyFile(documento.getKeyFile());
        documentoResponse.setFormato(documento.getFormato());
        documentoResponse.setDocumento(documento.getDocumento());
        return documentoResponse;
    }
}
