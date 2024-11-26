package com.admision.maestrias.api.pam.service.implementations;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.entity.CohorteEntity;
import com.admision.maestrias.api.pam.entity.EstadoEntity;
import com.admision.maestrias.api.pam.entity.UserEntity;
import com.admision.maestrias.api.pam.exceptions.EmailExistsException;
import com.admision.maestrias.api.pam.models.responses.AspiranteCohorteResponse;
import com.admision.maestrias.api.pam.repository.*;
import com.admision.maestrias.api.pam.service.interfaces.AspiranteServiceInterface;
import com.admision.maestrias.api.pam.shared.dto.AspiranteDTO;
import com.admision.maestrias.api.pam.shared.dto.UserDTO;
import com.amazonaws.services.kms.model.NotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las operaciones relacionadas con los aspirantes.
 * @author Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona, Angel Yesid Duque Cruz, Ingrid Florez, Javier Lopez
 */
@Service
public class AspiranteService implements AspiranteServiceInterface {

    @Autowired
    private AspiranteRepository aspiranteRepository;
    @Autowired
    private UserRepository usuarioRepository;
    @Autowired
    private EstadoRepository estadoRepository;
    @Autowired
    private CohorteRepository cohorteRepository;
    @Autowired
    private S3Repository s3Repository;
    @Autowired
    private NotificacionService notificacionService;

    /**
     * Registra un nuevo aspirante con los datos proporcionados.
     * 
     * @param aspirante El objeto ApplicantDTO que contiene los datos del
     *                  solicitante.
     * @return Un objeto ApplicantDTO que representa al nuevo solicitante
     *         registrado.
     * @throws EmailExistsException si el usuario correspondiente al ID
     *                              proporcionado
     *                              no se encuentra en la base de datos.
     */
    @Override
    public AspiranteDTO crearAspirante(AspiranteDTO aspirante, String email) throws EmailExistsException {
        UserEntity user = usuarioRepository.findByEmail(email);
        if (user.getAspirante() != null) {
            throw new EmailExistsException("Aspirante ya existe");
        }

        CohorteEntity cohorteAbierto = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteAbierto == null) {
            throw new EntityNotFoundException("No hay cohorte abierto");
        }
        AspiranteEntity newApplicant = null;
        try{
            AspiranteEntity aspiranteEntity = new AspiranteEntity();
            BeanUtils.copyProperties(aspirante, aspiranteEntity);
            aspiranteEntity.setCorreoPersonal(user.getEmail());
            aspiranteEntity.setUser(user);
            aspiranteEntity.setEstado(estadoRepository.findById(2).get());
            aspiranteEntity.setCohorte(cohorteAbierto);
            newApplicant = aspiranteRepository.save(aspiranteEntity);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar aspirante");
        }

        try{
            s3Repository.crearCarpeta(cohorteAbierto.getId() + "/" + newApplicant.getId().toString());
        }catch (Exception e) {
            System.out.println("ERROR::: " + e);
            aspiranteRepository.delete(newApplicant);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear carpeta");
        }

        AspiranteDTO newAspiranteDTO = new AspiranteDTO();
        BeanUtils.copyProperties(newApplicant, newAspiranteDTO);

        return newAspiranteDTO;
    }

    /**
     * Obtiene el usuario del aspirante
     * 
     * @param id ID del aspirante
     * @return el usuario que le pertenece al aspirante.
     */
    @Override
    public UserDTO getUserByAspirante(Integer id) {
        AspiranteEntity aspirante = aspiranteRepository.findById(id).get();
        UserEntity user = aspirante.getUser();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    /**
     * 
     * Este método se encarga de desactivar un aspirante en la base de datos
     * mediante su ID de usuario.
     * 
     * @param email el email del usuario.
     * @return Un objeto AspiranteDTO que representa al aspirante desactivado en la
     *         base de datos.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   de usuario proporcionado.
     */
    @Override
    public void disableAspirante(String email) {
        AspiranteEntity aspiranteEntity = aspiranteRepository.findByCorreoPersonal(email);
        if (aspiranteEntity == null) {
            throw new UsernameNotFoundException("Aspirante con el email " + email + " no fue encontrado");
        }
        aspiranteEntity.setEstado(estadoRepository.findByDescripcion("DESACTIVADO"));
        try {
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al desactivar aspirante");
        }
    }

    /**
     * Este método se encarga de activar un aspirante en la base de datos
     * mediante su ID de usuario.
     * 
     * @param email el email del usuario.
     * @return Un objeto AspiranteDTO que representa al aspirante desactivado en la
     *         base de datos.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   de usuario proporcionado.
     */
    @Override
    public void enableAspirante(String email) {
        AspiranteEntity applicantEntity = aspiranteRepository.findByCorreoPersonal(email);
        if (applicantEntity == null)
            throw new UsernameNotFoundException("Aspirante con el email " + email + " no fue encontrado");
        applicantEntity.setEstado(estadoRepository.findById(1).get());
        try {
            aspiranteRepository.save(applicantEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al activar aspirante");
        }
    }

    /**
     * Obtiene el aspirante a través del id de la cuenta.
     *
     * @param email email del usuario
     * @return @return un aspiranteDTO correspondiente al aspirante.
     */
    @Override
    public AspiranteCohorteResponse getAspiranteByUserEmail(String email) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findByUser_Email(email);
        if (!aspirante.isPresent()) {
            throw new UsernameNotFoundException("No existe ningún aspirante asociado al email.");
        }
        AspiranteEntity aspiranteEntity = aspirante.get();
        AspiranteCohorteResponse aspiranteRetornado = new AspiranteCohorteResponse();
        BeanUtils.copyProperties(aspiranteEntity, aspiranteRetornado);
        return aspiranteRetornado;
    }

    public AspiranteDTO getAspiranteByEmail(String email) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findByUser_Email(email);
        if (!aspirante.isPresent()) {
            throw new UsernameNotFoundException("No existe ningún aspirante asociado al email");
        }
        AspiranteEntity aspiranteEntity = aspirante.get();
        AspiranteDTO aspiranteRetornado = new AspiranteDTO();
        BeanUtils.copyProperties(aspiranteEntity, aspiranteRetornado);
        return aspiranteRetornado;
    }

    /**
     * busca un aspirante por id del aspirante
     * 
     * @param aspiranteId ID del aspirante
     * @return AspiranteDTO
     */
    @Override
    public AspiranteDTO getAspiranteByAspiranteId(Integer aspiranteId) {

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        AspiranteDTO aspiranteDTO = new AspiranteDTO();
        aspiranteDTO.setPuntajeDocumentos(aspiranteEntity.getPuntajeDocumentos());
        aspiranteDTO.setTotal(aspiranteEntity.getTotal());
        BeanUtils.copyProperties(aspiranteEntity, aspiranteDTO);
        return aspiranteDTO;
    }

    /**
     * Obtiene la lista de aspirantes para la cohorte actual.
     * Ordena a los aspirantes por nombre
     * 
     * @return Una lista de objetos AspiranteDTO que representan a los aspirantes de
     *         la cohorte actual.
     */
    @Override
    public List<AspiranteDTO> listarAspirantesCohorteActual() {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        List<AspiranteEntity> aspiranteEntities = new ArrayList<>(cohorteEntity.getAspirantes());
        aspiranteEntities.sort(Comparator.comparing(AspiranteEntity::getNombre));
        List<AspiranteDTO> aspirantes = aspiranteEntities.stream()
                .sorted(Comparator.comparing(AspiranteEntity::getNombre))
                .map(this::convertirAspiranteEntityADTO)
                .collect(Collectors.toList());
        return aspirantes;
    }

    /**
     * Obtiene el aspirante a través del id del aspirante.
     * 
     * @param id id del aspirante
     * @return un aspiranteDTO correspondiente al aspirante.
     */
    @Override
    public AspiranteDTO getAspiranteById(int id) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(id);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        AspiranteDTO aspiranteRetornado = new AspiranteDTO();
        BeanUtils.copyProperties(aspiranteEntity, aspiranteRetornado);
        return aspiranteRetornado;
    }

    /**
     * Busca un aspirante por id y le asigna la fecha de la entrevista
     *
     * @param id               id del aspirante
     * @param fecha_entrevista fecha de la entrevista
     */
    @Override
    public void habilitarFechaEntrevista(Integer id, LocalDateTime fecha_entrevista) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteEntity == null)
            throw new NotFoundException("No hay una cohorte habilitada");
        if (cohorteEntity.getEnlace_entrevista() == null)
            throw new NotFoundException("No hay un enlace de entrevista asignado");

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(id);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        try {
            aspiranteEntity.setFecha_entrevista(fecha_entrevista);
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al asignar fecha de entrevista");
        }
        AspiranteDTO aspiranteDTO = new AspiranteDTO();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy 'a las' HH:mm");
        String fechaFormateada = fecha_entrevista.format(formatter);

        notificacionService.crearNotificacion("Tiene una entrevista programada para el día " + fechaFormateada
            + " este es el enlace para acceder: " + cohorteEntity.getEnlace_entrevista(), aspiranteEntity.getId());
    }

    /**
     * Califica la prueba de un aspirante.
     *
     * @param id                 el ID del aspirante.
     * @param calificacionPrueba la calificación de la prueba.
     * @return un objeto AspiranteDTO con los datos actualizados del aspirante.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   proporcionado.
     * @throws IllegalArgumentException  si la calificación de la prueba no es
     *                                   válida.
     * @throws EntityNotFoundException   si no se encuentra un enlace y una fecha de
     *                                   prueba.
     */
    @Override
    public void calificarPruebaAspirante(int id, int calificacionPrueba) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(id);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        CohorteEntity cohorteEntity = aspiranteEntity.getCohorte();
        if(!cohorteEntity.getHabilitado()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cohorte no está habilitada");
        }

        if (calificacionPrueba < 0 || calificacionPrueba > 40)
            throw new IllegalArgumentException("La calificacion de la prueba no es valida");

        if (cohorteEntity.getEnlace_prueba() == null || cohorteEntity.getFechaMaxPrueba() == null)
            throw new EntityNotFoundException("No existe un enlace y fecha de prueba");

        try {
            aspiranteEntity.setPuntaje_prueba(calificacionPrueba);
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al calificar la prueba");
        }
    }

    /**
     * Califica las notas de pregrado de un aspirante.
     *
     * @param aspiranteId          El ID del aspirante.
     * @param puntajeCartas        Puntaje de las cartas de referencia.
     * @param puntajeNotasPregrado Puntaje de las notas de pregrado.
     * @param puntajePublicaciones Puntaje de las publicaciones y participaciones en congresos.
     * @param puntajeDistinciones  Puntaje de las distinciones académicas.
     * @param puntajeExperiencia   Puntaje de la experiencia laboral o investigativa.
     * @throws UsernameNotFoundException Si no se encuentra ningún aspirante
     *                                   asociado al ID proporcionado.
     * @throws IllegalArgumentException  Si alguna de las calificaciones no es
     *                                   válida.
     * @throws IllegalArgumentException  Si el aspirante no se encuentra en el
     *                                   estado adecuado para calificar los
     *                                   documentos.
     */
    @Override
    public void calificarDocsIndivi(Integer aspiranteId, Integer puntajeCartas, Integer puntajeNotasPregrado,
            Double puntajePublicaciones, Double puntajeDistinciones, Double puntajeExperiencia) {
        Optional<AspiranteEntity> aspiranteEntity = aspiranteRepository.findById(aspiranteId);
        if (!aspiranteEntity.isPresent())
            throw new UsernameNotFoundException("No existe ningun aspirante asociado.");
        AspiranteEntity aspirante = aspiranteEntity.get();

        if (puntajeNotasPregrado < 0 || puntajeNotasPregrado > 20)
            throw new IllegalArgumentException("La calificacion de las notas de pregrado no es valida");
        aspirante.setPuntajeNotas(puntajeNotasPregrado);

        if (puntajeCartas < 0 || puntajeCartas > 15)
            throw new IllegalArgumentException("La calificacion de las cartas de referencia no es valida");
        aspirante.setPuntajeCartasReferencia(puntajeCartas);

        if (puntajeDistinciones < 0 || puntajeDistinciones > 2.5)
            throw new IllegalArgumentException("La calificacion de las distinciones academicas no es valida");
        aspirante.setPuntajeDistincionesAcademicas(puntajeDistinciones);

        if (puntajePublicaciones < 0 || puntajePublicaciones > 5)
            throw new IllegalArgumentException(
                    "La calificacion de las publicaciones y participaciones en congresos no es valida");
        aspirante.setPuntajePublicaciones(puntajePublicaciones);

        if (puntajeExperiencia < 0 || puntajeExperiencia > 2.5)
            throw new IllegalArgumentException("La calificacion de la experiencia laboral  no es valida");
        aspirante.setPuntajeExperienciaLaboral(puntajeExperiencia);

        if (aspirante.getEstado().getId() != 4)
            throw new IllegalArgumentException(
                    "No se puede calificar los documentos porque el aspirante se encuentre en el estado "
                            + aspirante.getEstado().getDescripcion());

        aspiranteRepository.save(aspirante);
        actualizarEstadoAspirante(aspirante);

    }

    /**
     * Califica la entrevista de un aspirante.
     *
     * @param id                     el ID del aspirante.
     * @param calificacionEntrevista la calificación de la entrevista.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   proporcionado.
     * @throws IllegalArgumentException  si la calificación de la entrevista no es
     *                                   válida.
     * @throws EntityNotFoundException   si no se encuentra un enlace y una fecha de
     *                                   entrevista.
     */
    @Override
    public void calificarEntrevistaAspirante(int id, int calificacionEntrevista) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(id);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        CohorteEntity cohorteEntity = aspiranteEntity.getCohorte();
        if(!cohorteEntity.getHabilitado()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cohorte no está habilitada");
        }

        if (calificacionEntrevista < 0 || calificacionEntrevista > 15)
            throw new IllegalArgumentException("La calificacion de la entrevista no es valida");

        if (cohorteEntity.getEnlace_entrevista() == null || aspiranteEntity.getFecha_entrevista() == null)
            throw new EntityNotFoundException("No existe un enlace y fecha de entrevista");
        try {
            aspiranteEntity.setPuntaje_entrevista(calificacionEntrevista);
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al calificar la entrevista");
        }
    }

    /**
     * Admite a un aspirante actualizando su estado a "ADMITIDO".
     *
     * @param aspiranteId el ID del aspirante a admitir
     * @throws UsernameNotFoundException si no se encuentra el aspirante
     */
    @Override
    public void admitirAspirante(Integer aspiranteId) {

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();

        if (aspiranteEntity.getEstado().getId() == 5) {
            aspiranteEntity.setEstado(estadoRepository.findByDescripcion("ADMITIDO"));
            aspiranteRepository.save(aspiranteEntity);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El aspirante no se encuentra en el estado correcto para la admisión.");
        notificacionService.crearNotificacion("Haz sido admitido a la maestria", aspiranteEntity.getId());
    }

    /**
     * Rechaza la admisión de un aspirante dado su ID.
     *
     * @param aspiranteId El ID del aspirante a rechazar la admisión.
     * @throws UsernameNotFoundException si no se encuentra ningún aspirante
     *                                   asociado al ID proporcionado.
     * @throws ResponseStatusException   si el estado del aspirante no es válido
     *                                   para el rechazo de la admisión.
     */
    @Override
    public void rechazarAdmisionAspirante(Integer aspiranteId) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();

        if (aspiranteEntity.getEstado().getId() == 6) {
            aspiranteEntity.setEstado(estadoRepository.findById(5).get());
            aspiranteRepository.save(aspiranteEntity);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El aspirante no se encuentra admitido.");
    }

    /**
     * Obtiene una lista de los aspirantes admitidos.
     *
     * @return una lista de objetos AspiranteDTO de los aspirantes admitidos
     */
    @Override
    public List<AspiranteDTO> listarAdmitidos(Integer estadoId) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        List<AspiranteEntity> aspiranteEntities = new ArrayList<>(cohorteEntity.getAspirantes());
        List<AspiranteDTO> aspiranteDTOs = new ArrayList<>();

        for (AspiranteEntity aspiranteEntity : aspiranteEntities) {
            if (aspiranteEntity.getEstado().getId() == estadoId) {
                AspiranteDTO aspiranteDTO = new AspiranteDTO();
                BeanUtils.copyProperties(aspiranteEntity, aspiranteDTO);
                aspiranteDTOs.add(aspiranteDTO);
            }
        }

        return aspiranteDTOs;
    }

    /**
     * Mapea una aspirante Entity
     * 
     * @param aspiranteEntity Aspirante entity
     * @return AspiranteDTO
     */
    private AspiranteDTO convertirAspiranteEntityADTO(AspiranteEntity aspiranteEntity) {
        AspiranteDTO aspiranteDTO = new AspiranteDTO();
        BeanUtils.copyProperties(aspiranteEntity, aspiranteDTO);
        return aspiranteDTO;
    }

    /**
     * Obtiene una lista de aspirantes históricos para un cohorte específico.
     *
     * @param cohorteId El ID del cohorte para el cual se obtendrán los aspirantes
     *                  históricos.
     * @return Una lista de objetos AspiranteDTO que representan los aspirantes
     *         históricos del cohorte.
     * @throws IllegalArgumentException Si no se encuentra el cohorte con el ID
     *                                  especificado.
     */
    @Override
    public List<AspiranteDTO> obtenerAspirantesHistoricosCohorte(Integer cohorteId) {
        CohorteEntity cohorte = cohorteRepository.findById(cohorteId)
                .orElseThrow(() -> new IllegalArgumentException("Cohorte no encontrado"));

        List<AspiranteEntity> aspirantes = aspiranteRepository.findByCohorte(cohorte);

        List<AspiranteDTO> aspiranteDTOs = new ArrayList<>();
        for (AspiranteEntity aspirante : aspirantes) {
            AspiranteDTO aspiranteDTO = new AspiranteDTO();
            BeanUtils.copyProperties(aspirante, aspiranteDTO);
            aspiranteDTOs.add(aspiranteDTO);
        }
        return aspiranteDTOs;
    }

    /**
     * Asigna el estado de un aspirante a "ENTREVISTA Y PRUEBA" si al aspirante se le han calificado sus documentos
     *
     * @param aspirante
     */
    private void actualizarEstadoAspirante(AspiranteEntity aspirante) {
        if (aspirante.getPuntajeCartasReferencia() > 0 && aspirante.getPuntajeDistincionesAcademicas() > 0
                && aspirante.getPuntajeNotas() > 0 && aspirante.getPuntajeExperienciaLaboral() > 0
                && aspirante.getPuntajePublicaciones() > 0) {
            EstadoEntity estadoEntity = estadoRepository.findById(5).get();
            aspirante.setEstado(estadoEntity);
            aspiranteRepository.save(aspirante);
        }
    }

    /**
     * Cambia es egresado ufps a false
     * 
     * @param aspiranteId el id del aspirante al que se la va a cambiar es egresado
     *                    ufps
     * @return un objeto AspiranteDTO que contiene la información del aspirante
     */
    @Override
    public void cambiarEsEgresado(Integer aspiranteId) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if(!aspirante.isPresent()){
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        }
        AspiranteEntity aspiranteEntity = aspirante.get();
        aspiranteEntity.setEs_egresado_ufps(false);
        aspiranteRepository.save(aspiranteEntity);

    }

}
