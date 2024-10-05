package com.admision.maestrias.api.pam.service.implementations;

import com.admision.maestrias.api.pam.entity.*;
import com.admision.maestrias.api.pam.exceptions.EmailExistsException;
import com.admision.maestrias.api.pam.models.responses.AspiranteEstadoDocResponse;
import com.admision.maestrias.api.pam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Angel Yesid Duque Cruz, Miguel Angel Lara, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Service("documentoService")
public class DocumentoService {

    @Autowired
    DocumentoRepository documentoRepository;
    @Autowired
    EstadoDocRepository estadoDocRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RolRepository rolRepository;
    @Autowired
    TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired
    AspiranteRepository aspiranteRepository;
    @Autowired
    CohorteRepository cohorteRepository;
    @Autowired
    NotificacionService notificacionService;
    @Autowired
    EstadoRepository estadoRepository;

    /**
     * Este método se encarga de cambiar el estado de un documento en la base de
     * datos.
     * Busca el documento correspondiente al email y tipo de documento
     * proporcionados,
     * y actualiza su estado al nuevo estado especificado.
     *
     * @param tipodocId     el ID del tipo de documento.
     * @param nuevoEstadoId el ID del nuevo estado del documento.
     * @throws EmailExistsException si el aspirante no existe en la base de datos.
     */
    public void cambiarEstadoDocumento(Integer aspiranteId, Integer tipodocId, Integer nuevoEstadoId) {

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);

        if (!aspirante.isPresent()) {
            throw new EmailExistsException("El aspirante no existe");
        }

        DocumentoEntity documentoEntity = documentoRepository.findByAspiranteAndDocumento(aspiranteId, tipodocId);

        EstadoDocEntity estado = estadoDocRepository.findById(nuevoEstadoId).get();
        /*
         * si el documento antes de ser aceptado fue rechazado
         * se borra el comentario
         */
        if (documentoEntity.getEstado().getId() == 4) {
            documentoEntity.setRetroalimentacion("");
        }
        documentoEntity.setEstado(estado);

        documentoRepository.save(documentoEntity);

        String notificacion = nuevoEstadoId == 3 ? "El documento " + documentoEntity.getDocumento().getNombre() + " ha sido rechazado"
                : "El documento " + documentoEntity.getDocumento().getNombre() + " ha sido aprobado";
        if (nuevoEstadoId == 3) {
            notificacionService.crearNotificacion(notificacion, aspiranteId);
        }

        boolean todosaprobados = true;
        int cantidadDocumentos = documentoRepository.countByAspirante(aspirante.get());
        if (aspirante.get().getDocumentos().size() == cantidadDocumentos) { //se pregunta si el aspirante tiene los docs que debería tener
            for (DocumentoEntity doc : aspirante.get().getDocumentos()) {
                if (doc.getEstado().getId() != 4) {
                    todosaprobados = false;
                    break; // con solo un doc que este rechazado, ya no es necesario seguir con el ciclo. 
                }
            }
            if (todosaprobados) {
                notificacion = "Todos sus documentos han sido aceptados";
                aspirante.get().setEstado(estadoRepository.findById(4).get());
                aspiranteRepository.save(aspirante.get());
                notificacionService.crearNotificacion(notificacion, aspiranteId);
            }
        }
    }

    /**
     * Este método lista los documentos asociados a un aspirante.
     *
     * @return Una lista de objetos DocumentoEntity que representa los documentos
     * del aspirante.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID de usuario proporcionado.
     */
    public List<DocumentoEntity> listarDocumentosPorAspirante(String email) {

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findByUser_Email(email);
        if (!aspirante.isPresent()) {
            throw new UsernameNotFoundException("No existe ningún aspirante asociado al email");
        }
        AspiranteEntity aspiranteEntity = aspirante.get();
        List<DocumentoEntity> documentos = documentoRepository.findByAspirante(aspiranteEntity);

        for (DocumentoEntity documento : documentos) {
            EstadoDocEntity estado = estadoDocRepository.findById(documento.getEstado().getId()).orElse(null);
            documento.setEstado(estado);
        }

        return documentos;
    }

    /**
     * Este método lista los documentos asociados a un aspirante.
     *
     * @param aspiranteId el ID del aspirante.
     * @return Una lista de objetos DocumentoEntity que representa los documentos
     * del aspirante.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   de usuario proporcionado.
     */
    public List<DocumentoEntity> listarDocumentosDeAspirante(Integer aspiranteId) {
        Optional<AspiranteEntity> aspiranteEntity = aspiranteRepository.findById(aspiranteId);

        if (!aspiranteEntity.isPresent()) {
            throw new UsernameNotFoundException("No existe ningun aspirante asociado.");
        }

        AspiranteEntity aspirante = aspiranteEntity.get();

        return documentoRepository.findByAspirante(aspirante).stream()
                .sorted(Comparator.comparing(doc -> ((DocumentoEntity) doc).getDocumento().getId()))
                .collect(Collectors.toList());

    }

    /**
     * Este método se encarga de enviar la retroalimentación para un documento
     * específico.
     *
     * @param tipoDocId         el ID del tipo de documento.
     * @param retroalimentacion la retroalimentación a enviar.
     */

    public DocumentoEntity EnviarRetroalimentacion(Integer aspiranteId, Integer tipoDocId, String retroalimentacion) {

        Optional<AspiranteEntity> aspiranteEntity = aspiranteRepository.findById(aspiranteId);

        if (!aspiranteEntity.isPresent()) {
            throw new UsernameNotFoundException("No existe ningun aspirante asociado.");
        }
        try {
            DocumentoEntity doc = documentoRepository.findByAspiranteAndDocumento(aspiranteId, tipoDocId);
            doc.setEstado(estadoDocRepository.findById(3).get());
            // Actualizar la retroalimentación del documento
            doc.setRetroalimentacion(retroalimentacion);
            return documentoRepository.save(doc);
        } catch (Exception e) {
            throw new UsernameNotFoundException("El documento no existe");
        }
    }

    /**
     * Este método se encarga de listar los aspirantes que tienen un estado de
     * documento específico.
     *
     * @param idEstado el ID del estado del documento.
     * @return Una lista de objetos AspiranteEstadoDocResponse que representan los
     * aspirantes con el estado de documento
     */
    public List<AspiranteEstadoDocResponse> listarAspirantesConEstadoDoc(Integer idEstado) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        List<AspiranteEntity> aspiranteEntities = new ArrayList<>(cohorteEntity.getAspirantes());

        List<AspiranteEstadoDocResponse> aspirantes = new ArrayList<>();

        for (AspiranteEntity aspiranteEntity : aspiranteEntities) {

            AspiranteEstadoDocResponse aspiranteEstado = new AspiranteEstadoDocResponse();
            int nDocs = documentoRepository.findDocumentosByEstado(idEstado, aspiranteEntity.getId()).size();
            aspiranteEstado.setNombre(aspiranteEntity.getNombre());
            aspiranteEstado.setApellido(aspiranteEntity.getApellido());
            aspiranteEstado.setCorreoPersonal(aspiranteEntity.getCorreoPersonal());
            aspiranteEstado.setTelefono(aspiranteEntity.getTelefono());

            if (nDocs == 10 && idEstado == 4) {
                EstadoDocEntity estadoDocEntity = estadoDocRepository.findById(4).get();
                aspiranteEstado.setEstado(estadoDocEntity);
                aspirantes.add(aspiranteEstado);
            } else if (idEstado == 2 && nDocs == 10) {
                EstadoDocEntity estadoDocEntity = estadoDocRepository.findById(2).get();
                aspiranteEstado.setEstado(estadoDocEntity);
                aspirantes.add(aspiranteEstado);
            } else if ((idEstado == 3 && nDocs > 0 && nDocs < 10) || (idEstado == 3 && nDocs == 10)) {
                EstadoDocEntity estadoDocEntity = estadoDocRepository.findById(3).get();
                aspiranteEstado.setEstado(estadoDocEntity);
                aspirantes.add(aspiranteEstado);
            }

        }
        return aspirantes;
    }

    /**
     * Este método se encarga de crear los documentos para un aspirante específico.
     * Verifica el estado del aspirante y crea documentos asociados a él en función
     * de los tipos de documentos existentes.
     *
     * @param aspiranteId el ID del aspirante para el cual se crearán los
     *                    documentos.
     * @return Una lista de objetos DocumentoEntity que representan los documentos
     * creados para el aspirante.
     * Si el aspirante no existe o su estado no es "ENVIO DOCUMENTOS", se
     * devuelve una lista vacía.
     */
    public List<DocumentoEntity> crearDocumentos(Integer aspiranteId) {
        AspiranteEntity aspiranteEntity = aspiranteRepository.findById(aspiranteId).orElse(null);

        if (aspiranteEntity == null)
            return new ArrayList<>();

        EstadoEntity estadoEntity = aspiranteEntity.getEstado();
        if (!"ENVIO DOCUMENTOS".equals(estadoEntity.getDescripcion()))
            return new ArrayList<>();

        List<TipoDocumentoEntity> tiposDocumentos = tipoDocumentoRepository.findAll();
        tiposDocumentos.sort(Comparator.comparing(TipoDocumentoEntity::getId));

        List<DocumentoEntity> documentos = new ArrayList<>();

        EstadoDocEntity estadoNoSubido = estadoDocRepository.findById(1).get();
        if (aspiranteEntity.getEs_egresado_ufps())//es egresado
            filtrarDocumentosEgresado(tiposDocumentos);
        if (!aspiranteEntity.getLugar_nac().equals("Colombia"))//es extranjero
            filtrarDocumentosExtranjero(tiposDocumentos);
        else
            filtrarDocumentosNacional(tiposDocumentos);

        for (TipoDocumentoEntity tipoDocumento : tiposDocumentos) {
            DocumentoEntity documentoEntity = new DocumentoEntity();
            documentoEntity.setAspirante(aspiranteEntity);
            documentoEntity.setDocumento(tipoDocumento);
            documentoEntity.setEstado(estadoNoSubido);
            documentoEntity.setUrl(" ");
            documentoEntity.setKeyFile(" ");
            documentoEntity.setRetroalimentacion(" ");
            documentoEntity.setFormato(" ");
            documentoRepository.save(documentoEntity);
            documentos.add(documentoEntity);
        }
        return documentos;
    }

    /**
     * Metodo que se usa cuando el admin le cambio es_egresado_ufps a false para agregar
     * el nuevo documento que debe enviar el aspirante
     *
     * @param aspiranteId id del aspirante que ahora debe enviar un documento más
     */
    public void agregarNotasPregrado(Integer aspiranteId) {
        AspiranteEntity aspiranteEntity = aspiranteRepository.findById(aspiranteId).get();
        TipoDocumentoEntity tipoDocumentoEntity = tipoDocumentoRepository.findById(4).get();
        EstadoDocEntity estadoNoSubido = estadoDocRepository.findById(1).get();
        DocumentoEntity documentoEntity = new DocumentoEntity();
        documentoEntity.setAspirante(aspiranteEntity);
        documentoEntity.setDocumento(tipoDocumentoEntity);
        documentoEntity.setEstado(estadoNoSubido);
        documentoEntity.setUrl(" ");
        documentoEntity.setKeyFile(" ");
        documentoEntity.setRetroalimentacion(" ");
        documentoEntity.setFormato(" ");
        documentoRepository.save(documentoEntity);

    }

    /**
     * Elimina los documentos que un egresado no debería subir
     *
     * @param tiposDoc la lista con los documentos que debe subir el aspirante
     */
    private void filtrarDocumentosEgresado(List<TipoDocumentoEntity> tiposDoc) {
        tiposDoc.remove(tipoDocumentoRepository.findById(3).get());
    }

    /**
     * Elimina los documentos que un aspirante nacional(que no es extranjero) no debería subir
     *
     * @param tiposDoc la lista con los documentos que debe subir el aspirante
     */
    private void filtrarDocumentosNacional(List<TipoDocumentoEntity> tiposDoc) {
        tiposDoc.remove(tipoDocumentoRepository.findById(11).get());
        tiposDoc.remove(tipoDocumentoRepository.findById(12).get());
        tiposDoc.remove(tipoDocumentoRepository.findById(13).get());
    }

    /**
     * Elimina los documentos que un aspirante no debería subir
     *
     * @param tiposDoc la lista con los documentos que debe subir el aspirante
     */
    private void filtrarDocumentosExtranjero(List<TipoDocumentoEntity> tiposDoc) {
        tiposDoc.remove(tipoDocumentoRepository.findById(2).get());
        tiposDoc.remove(tipoDocumentoRepository.findById(3).get());
        tiposDoc.remove(tipoDocumentoRepository.findById(4).get());
    }
}