package com.admision.maestrias.api.pam.controller;

import com.admision.maestrias.api.pam.entity.DocumentoEntity;
import com.admision.maestrias.api.pam.models.requests.AspiranteDocumentoRequest;
import com.admision.maestrias.api.pam.models.requests.AspiranteEntrevistaRequest;
import com.admision.maestrias.api.pam.models.requests.AspirantePruebaRequest;
import com.admision.maestrias.api.pam.models.requests.AspiranteRequest;
import com.admision.maestrias.api.pam.models.responses.*;
import com.admision.maestrias.api.pam.service.implementations.AspiranteService;
import com.admision.maestrias.api.pam.service.implementations.CohorteService;
import com.admision.maestrias.api.pam.service.implementations.DocumentoService;
import com.admision.maestrias.api.pam.service.implementations.EmailService;
import com.admision.maestrias.api.pam.shared.dto.AspiranteDTO;
import com.admision.maestrias.api.pam.shared.dto.CohorteDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona, Angel Yesid Duque Cruz, Ingrid Neileth FLorez Garay
 */
@RestController
@RequestMapping("/aspirante")
public class AspiranteController {

    @Autowired
    AspiranteService aspiranteService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CohorteService cohorteService;

    @Autowired
    private DocumentoService documentoService;

    /**
     * Endpoint que agrega la información de un aspirante, es el primer formulario
     * que se realiza para la inscripción
     * donde está toda la información básica del aspirante y se asocia a la cuenta
     * de un usuario con rol aspirante.
     *
     * @param aspiranteRequest información personal, laboral y académica del
     *                         aspirante
     * @return obj json con un "message" que indica si fue éxito el registro o no
     */
    @Secured("ROLE_USUARIO")
    @Transactional
    @PostMapping
    public ResponseEntity<AnyResponse> crearAspirante(@RequestBody @Valid AspiranteRequest aspiranteRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correoPersonal = authentication.getPrincipal().toString();

        AspiranteDTO aspiranteDTO = new AspiranteDTO();
        BeanUtils.copyProperties(aspiranteRequest, aspiranteDTO);
        AspiranteDTO createdApplicant = aspiranteService.crearAspirante(aspiranteDTO, correoPersonal);

        try {
            List<DocumentoEntity> documentos = documentoService.crearDocumentos(createdApplicant.getId());
            String message = (!documentos.isEmpty())
                    ? "Formulario de inscripción enviado y documentos del aspirante creados exitosamente."
                    : "No se pudo realizar la inscripción.";
            emailService.sendListEmail(correoPersonal, message, "");
            return ResponseEntity.ok().body(new AnyResponse(message));
        } catch (Exception e) {
            throw new RuntimeException("Error al crear los documentos del aspirante", e);
        }
    }

    /**
     * Endpoint Obtener toda la información de un aspirante
     * Obtiene un objeto AspiranteCohorteResponse que representa al aspirante con el
     * id de la
     * cuenta asociada a ese aspirante.
     *
     * @return Un objeto AspiranteDTO que representa al aspirante encontrado en la
     *         base de datos.
     */
    @Secured("ROLE_USUARIO")
    @GetMapping("/obtener")
    public AspiranteCohorteResponse getAspiranteById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        return aspiranteService.getAspiranteByUserEmail(email);
    }

    /**
     * DUPLICADO PERO SE IMPLEMENTA PARA NO DAÑAR MÉTODO YA IMPLEMENTADO EN EL FRONT
     * Método que recupera toda la información de un aspirante.
     *
     * @param id id del usuario del aspirante.
     * @return AspiranteDTO
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @GetMapping("/info")
    public AspiranteDTO getAspiranteDetallado(@RequestParam Integer id) {
        AspiranteDTO aspiranteResponse = new AspiranteDTO();
        BeanUtils.copyProperties(aspiranteService.getAspiranteByAspiranteId(id), aspiranteResponse);
        return aspiranteResponse;
    }

    /**
     * Endpoint para obtener la información de un aspirante por su id
     * Solo accesible por usuarios con los roles administrador y encargado.
     *
     * @param id id del aspirante
     * @return la información personal de un aspirante resumida.
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @GetMapping
    public AspiranteCohorteResponse getAspiranteInfo(@RequestParam Integer id) {
        AspiranteCohorteResponse aspiranteResponse = new AspiranteCohorteResponse();
        BeanUtils.copyProperties(aspiranteService.getAspiranteById(id), aspiranteResponse);
        return aspiranteResponse;
    }

    /**
     * Endpoint Desactivar Aspirante
     * Desactiva al aspirante con el correo electrónico proporcionado.
     * Solo accesible por un usuario con el rol administrador.
     *
     * @param emailAspirante el correo electrónico del aspirante.
     * @return El mensaje de éxito o error de la
     *         operación.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/{emailAspirante}/desactivar")
    public ResponseEntity<AnyResponse> desactivarAspirante(@PathVariable String emailAspirante) {
            aspiranteService.disableAspirante(emailAspirante);
            return ResponseEntity.ok(new AnyResponse("El aspirante ha sido desactivado exitosamente."));
    }

    /**
     * Endpoint Activar Aspirante
     * Activa al aspirante con el correo electrónico proporcionado.
     * Solo accesible por un usuario con el rol administrador.
     *
     * @param emailAspirante el correo electrónico del aspirante.
     * @return El mensaje de éxito o error de la operación.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/{emailAspirante}/activar")
    public ResponseEntity<AnyResponse> enableApplicant(@PathVariable String emailAspirante) {
        aspiranteService.enableAspirante(emailAspirante);
        return ResponseEntity.ok(new AnyResponse("El aspirante ha sido activado exitosamente."));
    }

    /**
     * Endpoint Listar Aspirantes por Cohorte
     * Obtiene una lista de aspirantes de la cohorte actual.
     * Solo accesible por usuarios con los roles administrador y encargado.
     *
     * @return Una lista de objetos AspiranteCohorteResponse que representan a los
     *         aspirantes de la cohorte actual.
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @GetMapping("/listar")
    public List<AspiranteCohorteResponse> listarAspirantesCohorteActual(@RequestParam(value = "filtro", required = false) Integer filtro) {
        List<AspiranteDTO> aspiranteDtos = aspiranteService.listarAspirantesCohorteActual();
        List<AspiranteCohorteResponse> aspirantesResponse = aspiranteDtos.stream()
                .filter(aspiranteDTO -> filtro == null || aspiranteDTO.getEstado().getId().equals(filtro))
                .map(aspiranteDTO -> {
                    AspiranteCohorteResponse aspiranteResponse = new AspiranteCohorteResponse();
                    BeanUtils.copyProperties(aspiranteDTO, aspiranteResponse);
                    return aspiranteResponse;
                })
                .collect(Collectors.toList());
        return aspirantesResponse;
    }

    /**
     * Endpoint para listar las calificaciones de los aspirantes.
     *
     * @return una lista de objetos CalificacionesResponse que contienen la
     *         información de las calificaciones de los aspirantes.
     *         Cada objeto CalificacionesResponse representa un aspirante y contiene
     *         los campos correspondientes a las calificaciones.
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/listarCalificaciones")
    public List<AdmitidosResponse> listarCalificacionesAspirantes() {
        List<AspiranteDTO> aspiranteDTOs = aspiranteService.listarAdmitidos(5);
        return aspiranteDTOs.stream()
                .map(this::mapAdmitidosResponse)
                .collect(Collectors.toList());
    }

    /***
     * Endpoint para asignar la fecha de la entrevista a un aspirante
     * Solo accesible por usuarios con rol administrador.
     *
     * @param id               id del aspirante
     * @param fecha_entrevista fecha de la entrevista a asignar
     * @return objeto aspiranteDTO que devuelve toda la información del aspirante
     *         y la fecha de la entrevista
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/horarioEntrevista")
    public ResponseEntity<AnyResponse> horarioEntrevista(@RequestParam Integer id, @RequestParam String fecha_entrevista) {
        LocalDateTime fechaentrevista = LocalDateTime.parse(fecha_entrevista, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        aspiranteService.habilitarFechaEntrevista(id, fechaentrevista);
        return ResponseEntity.ok(new AnyResponse("la fecha de entrevista del aspirante fue asignada con exito")) ;
    }

    /**
     * Endpoint para mostrar los enlaces de la prueba, la entrevista y las fechas
     * Solo accesible por usuarios con rol de aspirante.
     *
     * @return El objeto entrevistaprueba que contiene los datos de la prueba y
     *         la entrevista para el aspirante.
     */
    @Secured("ROLE_USUARIO")
    @Transactional
    @GetMapping("/entrevistaPrueba")
    public EntrevistaPruebaResponse entrevistaPrueba() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        AspiranteDTO aspiranteDTO = aspiranteService.getAspiranteByEmail(email);
        CohorteDTO cohorte = cohorteService.comprobarCohorte();

        EntrevistaPruebaResponse entrevistaprueba = new EntrevistaPruebaResponse();
        BeanUtils.copyProperties(cohorte, entrevistaprueba);
        entrevistaprueba.setFecha_entrevista(aspiranteDTO.getFecha_entrevista());
        return entrevistaprueba;
    }

    /**
     * Endpoint que califica las notas de pregrado de un aspirante.
     *
     * @param aspiranteDocumentoRequest la solicitud de cada puntaje de los
     *                                  documentos del
     *                                  aspirante
     * @return la respuesta del proceso de calificación
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/calificacionDocumentos")
    public ResponseEntity<AnyResponse> calificarDocsIndiv(
            @RequestBody @Valid AspiranteDocumentoRequest aspiranteDocumentoRequest) {
        aspiranteService.calificarDocsIndivi(aspiranteDocumentoRequest.getId(),
                aspiranteDocumentoRequest.getPuntajeCartasReferencia(),
                aspiranteDocumentoRequest.getPuntajeNotasPregrado(),
                aspiranteDocumentoRequest.getPuntajePublicaciones(),
                aspiranteDocumentoRequest.getPuntajeDistincionesAcad(),
                aspiranteDocumentoRequest.getPuntajeExperiencia());
        return ResponseEntity.ok(new AnyResponse("Los documentos del aspirante fueron calificadas exitosamente."));
    }

    /**
     * Endpoint para calificar la prueba de un aspirante.
     *
     * @param aspirantePruebaRequest el objeto de solicitud que contiene el ID del
     *                               aspirante y la calificación de la prueba.
     * @return un objeto AnyResponse que indica el resultado de la operación.
     *         Si la calificación de la prueba del aspirante se realizó con éxito,
     *         el mensaje será "La prueba del aspirante fue calificada
     *         exitosamente."
     *         Si no se pudo calificar la prueba del aspirante, el mensaje será "No
     *         se pudo calificar la prueba del aspirante."
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/calificacionPrueba")
    public ResponseEntity<AnyResponse> calificarPruebaAspirante(@RequestBody @Valid AspirantePruebaRequest aspirantePruebaRequest) {
        aspiranteService.calificarPruebaAspirante(aspirantePruebaRequest.getId(),
                aspirantePruebaRequest.getPuntaje_prueba());
        return ResponseEntity.ok(new AnyResponse("La prueba del aspirante fue calificada exitosamente."));
    }

    /**
     * Endpoint para calificar la prueba de un aspirante.
     *
     * @param aspiranteEntrevistaRequest el objeto de solicitud que contiene el ID
     *                                   del aspirante y la calificación de la
     *                                   prueba.
     * @return un objeto AnyResponse que indica el resultado de la operación.
     *         Si la calificación de la prueba del aspirante se realizó con éxito,
     *         el mensaje será "La prueba del aspirante fue calificada
     *         exitosamente."
     *         Si no se pudo calificar la prueba del aspirante, el mensaje será "No
     *         se pudo calificar la prueba del aspirante."
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/calificacionEntrevista")
    public ResponseEntity<AnyResponse> calificarEntrevistaAspirante(
            @RequestBody @Valid AspiranteEntrevistaRequest aspiranteEntrevistaRequest) {
        aspiranteService.calificarEntrevistaAspirante(aspiranteEntrevistaRequest.getId(),
                aspiranteEntrevistaRequest.getPuntaje_entrevista());
        return ResponseEntity.ok(new AnyResponse("La entrevista del aspirante fue calificada exitosamente."));
    }

    /**
     * Admite a un aspirante, actualizando su estado a "ADMITIDO".
     * Este endpoint requiere el rol de administrador (ROLE_ADMIN).
     *
     * @param aspiranteId el ID del aspirante a admitir
     * @return un objeto AnyResponse con un mensaje indicando el resultado de la
     *         operación.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/admitir")
    public ResponseEntity<AnyResponse> admitirAspirante(@RequestParam Integer aspiranteId) {
        aspiranteService.admitirAspirante(aspiranteId);
        return ResponseEntity.ok(new AnyResponse("El aspirante ha sido admitido exitosamente."));
    }

    /**
     * Obtiene una lista de los aspirantes admitidos.
     * Este endpoint requiere el rol de administrador (ROLE_ADMIN).
     *
     * @return una lista de objetos AdmitidosResponse con la información de los
     *         aspirantes admitidos
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/admitidos")
    public List<AdmitidosResponse> listarAdmitidos() {
        List<AspiranteDTO> aspiranteDTOs = aspiranteService.listarAdmitidos(6);
        return aspiranteDTOs.stream()
                .map(this::mapAdmitidosResponse)
                .collect(Collectors.toList());
    }

    /**
     * Endpoint para obtener la lista de aspirantes históricos de un cohorte.
     *
     * @param cohorteId El ID del cohorte del cual se obtendrán los aspirantes
     *                  históricos.
     * @return Una lista de objetos CohorteAspirantesResponse que representan los
     *         aspirantes históricos del cohorte.
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/historicos")
    public List<CohorteAspirantesResponse> obtenerAspirantesHistoricosCohorte(@RequestParam Integer cohorteId) {
        List<AspiranteDTO> aspirantes = aspiranteService.obtenerAspirantesHistoricosCohorte(cohorteId);
        return aspirantes.stream()
                .map(this::mapCohorteAspirantesResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un aspiranteDTO a un AdmitidoResponse
     *
     * @param aspiranteDTO el aspiranteDTO que desea convertir a AdmitidoResponse
     * @return AdmitidoResponse
     */
    private AdmitidosResponse mapAdmitidosResponse(AspiranteDTO aspiranteDTO) {
        AdmitidosResponse admitidosResponse = new AdmitidosResponse();
        BeanUtils.copyProperties(aspiranteDTO, admitidosResponse);
        admitidosResponse.setPuntaje_documentos(aspiranteDTO.getPuntajeDocumentos());
        admitidosResponse.setTotal_puntaje(aspiranteDTO.getTotal());
        return admitidosResponse;
    }

    /**
     * Convertir un aspiranteDTO a un CohorteAspirantesResponse
     *
     * @param aspirante AspiranteDTO para convertir en CohorteAspirantesResponse
     * @return CohorteAspirantesResponse
     */
    private CohorteAspirantesResponse mapCohorteAspirantesResponse(AspiranteDTO aspirante) {
        CohorteAspirantesResponse aspiranteResponse = new CohorteAspirantesResponse();
        BeanUtils.copyProperties(aspirante, aspiranteResponse);
        return aspiranteResponse;
    }

    /**
     * Obtiene las calificaciones de un aspirante según su ID.
     *
     * @param aspiranteId El ID del aspirante del cual se desean obtener las
     *                    calificaciones.
     * @return El objeto CalificacionesResponse que contiene las calificaciones del
     *         aspirante.
     */
    @GetMapping("/calificacionesAspirante")
    public CalificacionesResponse listarCalificacionesDeAspirante(@RequestParam Integer aspiranteId) {
        AspiranteDTO aspiranteDTO = aspiranteService.getAspiranteByAspiranteId(aspiranteId);
        CalificacionesResponse response = new CalificacionesResponse();
        BeanUtils.copyProperties(aspiranteDTO, response);
        response.setTotal(aspiranteDTO.getTotal());
        return response;
    }

    /**
     * Rechaza la admisión de un aspirante.
     *
     * @param aspiranteId El ID del aspirante cuya admisión se desea rechazar.
     * @return Un objeto AnyResponse que contiene un mensaje indicando si se pudo
     *         rechazar la admisión o no.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/rechazarAdmision")
    public ResponseEntity<AnyResponse> rechazarAdmisionAspirante(@RequestParam Integer aspiranteId) {
        aspiranteService.rechazarAdmisionAspirante(aspiranteId);
        return ResponseEntity.ok(new AnyResponse("Al aspirante le ha sido rechazada su admision"));
    }

    /**
     * Cambia el el campo es_egresado_ufps a false
     * 
     * @param aspiranteId El id del aspirante al que se le va a cambiar
     *                    es_egresao_ufps
     * @return Un objeto AnyResponse que contiene un mensaje indicando si se pudo
     *         cambiar o no
     *         es_egresado_ufps
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/cambiarEsEgresado")
    public ResponseEntity<AnyResponse> cambiarEsEgresado(@RequestParam Integer aspiranteId) {
        aspiranteService.cambiarEsEgresado(aspiranteId);
        documentoService.agregarNotasPregrado(aspiranteId);
        return ResponseEntity.ok(new AnyResponse("Estado de egresado cambiado correctamente"));
    }

}
