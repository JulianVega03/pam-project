package com.admision.maestrias.api.pam.service.implementations;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.admision.maestrias.api.pam.exceptions.CohorteExistException;
import com.admision.maestrias.api.pam.repository.S3Repository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.admision.maestrias.api.pam.entity.CohorteEntity;
import com.admision.maestrias.api.pam.repository.CohorteRepository;
import com.admision.maestrias.api.pam.service.interfaces.CohorteServiceInterface;
import com.admision.maestrias.api.pam.shared.dto.CohorteDTO;

import javax.annotation.PostConstruct;

/**
 * @author Juan Pablo Correa Tarazona, Angel Yesid Duque Cruz, Gibson Arbey, Julian Camilo Riveros Fonseca, Ingrid Florez
 */
@Service
public class CohorteService implements CohorteServiceInterface {
    @Autowired
    private CohorteRepository cohorteRepository;
    @Autowired
    private S3Repository s3Repository;
    @Autowired
    private NotificacionService notificacionService;

    private static final String FILE_PATH = "localDateTime.txt";

    /**
     * Abre una nueva cohorte en el sistema con los datos proporcionados.
     * 
     * @param cohorte El objeto CohorteDTO que contiene los datos de la cohorte a
     *                abrir.
     * @throws CohorteExistException si ya hay una cohorte abierta en el sistema.
     */
    @Override
    public void abrirCohorte(CohorteDTO cohorte) {
        CohorteEntity newCohorte = new CohorteEntity();

        Date fechaInicio = cohorte.getFechaInicio();
        Date fechaFin = cohorte.getFechaFin();
        if (fechaFin.before(fechaInicio))
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        if (fechaFin.before(new Date()))
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha actual.");

        List<CohorteEntity> cohortesInRange = cohorteRepository.findCohortesInRange(fechaInicio, fechaFin);

        if (cohortesInRange.isEmpty()) {
            CohorteEntity cohorteEntity = new CohorteEntity();
            cohorteEntity.setFechaInicio(fechaInicio);
            cohorteEntity.setFechaFin(fechaFin);
            if (fechaInicio.before(new Date()) && fechaFin.after(new Date())) {
                cohorteEntity.setHabilitado(true);
                cerrarCohorteAuto();
            } else {
                cohorteEntity.setHabilitado(false);
                abrirCohorteAuto();
            }
            newCohorte = cohorteRepository.save(cohorteEntity);
        } else {
            throw new IllegalArgumentException("El rango de fechas del cohorte se superpone con otros cohortes existentes.");
        }

        s3Repository.crearCarpeta(newCohorte.getId().toString());

    }

    /**
     * Cierra la cohorte actual en el sistema. Establece la propiedad "habilitado"
     * de la cohorte a falso,
     * asigna la fecha de finalización actual y guarda los cambios en la base de
     * datos.
     */
    @Override
    public void cerrarCohorte() {
        CohorteEntity cohorteEntity;
        cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        cohorteEntity.setHabilitado(false);
        Date fecha = new Date();
        cohorteEntity.setFechaFin(fecha);
        cohorteRepository.save(cohorteEntity);
    }

    /**
     * Obtiene la cohorte actual en el sistema.
     *
     * @return la cohorte actual.
     */
    @Override
    public CohorteDTO comprobarCohorte() {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteEntity == null)
            throw new IllegalArgumentException("No hay una cohorte abierta en el sistema.");
        CohorteDTO CohorteDTO = new CohorteDTO();
        BeanUtils.copyProperties(cohorteEntity, CohorteDTO);
        return CohorteDTO;
    }

    /**
     * Obtiene una lista de todas las cohortes disponibles.
     * 
     * @return Una lista de objetos CohorteDTO que representan las cohortes
     *         encontradas.
     */
    @Override
    public List<CohorteDTO> listarCohorte() {
        List<CohorteEntity> cohorteEntityList;
        cohorteEntityList = cohorteRepository.findAll();
        List<CohorteDTO> cohorteDTOReturn = new ArrayList<>();

        for (CohorteEntity cohorteEntity : cohorteEntityList) {
            CohorteDTO cohorteDTO = new CohorteDTO();
            BeanUtils.copyProperties(cohorteEntity, cohorteDTO);
            cohorteDTOReturn.add(cohorteDTO);
        }

        return cohorteDTOReturn;
    }

    /**
     * Guarda el enlace de la entrevista de la cohorte abierta.
     *
     * @param enlace un String que contiene el enlace de la entrevista
     *
     */
    @Override
    public void habilitarEnlace(String enlace) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if(cohorteEntity == null)
            throw new IllegalArgumentException("No hay una cohorte abierta en el sistema.");
        cohorteEntity.setEnlace_entrevista(enlace);
        cohorteRepository.save(cohorteEntity);
    }

    /**
     * Guarda el enlace y la fecha límite de la prueba de la cohorte abierta.
     *
     * @param enlace         el enlace de la prueba
     * @param fechaMaxPrueba la fecha y hora máxima para realizar la prueba
     * @return el objeto CohorteDTO actualizado con los datos de la cohorte
     *         habilitada para la prueba
     */
    @Override
    public CohorteDTO habilitarPrueba(String enlace, LocalDateTime fechaMaxPrueba) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if(cohorteEntity == null)
            throw new IllegalArgumentException("No hay una cohorte abierta en el sistema.");

        cohorteEntity.setEnlace_prueba(enlace);
        cohorteEntity.setFechaMaxPrueba(fechaMaxPrueba);
        cohorteRepository.save(cohorteEntity);

        CohorteDTO prueba = new CohorteDTO();
        BeanUtils.copyProperties(cohorteEntity, prueba);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy 'a las' HH:mm");
        String fechaFormateada = fechaMaxPrueba.format(formatter);

        try {
            notificacionService.crearNotificacion(
                    "Tiene la prueba programada tiene como plazo maximo para realizarla hasta el día " + fechaFormateada +
                            " este es el enlace para realizarla: " + prueba.getEnlace_prueba(),
                    0);
            return prueba;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la notificacion");
        }
    }

    /**
     * Amplía la fecha de finalización de un cohorte existente.
     *
     * @param nuevaFechaFin La nueva fecha de finalización del cohorte.
     *         actualizado.
     * @throws IllegalArgumentException Si la nueva fecha de finalización no es
     *                                  posterior a la fecha de finalización actual
     *                                  del cohorte.
     * @throws RuntimeException         Si no hay un cohorte actualmente habilitado.
     */
    @Override
    public void ampliarFechaFinCohorte(Date nuevaFechaFin) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteEntity == null) {
            throw new RuntimeException("No hay un cohorte actualmente habilitado.");
        }
        Date fechaFinActual = cohorteEntity.getFechaFin();
        if (nuevaFechaFin.after(fechaFinActual)) {
            cohorteEntity.setFechaFin(nuevaFechaFin);
            cohorteRepository.save(cohorteEntity);
        } else {
            throw new IllegalArgumentException("La nueva fecha de fin debe ser posterior a la fecha de fin actual.");
        }
    }

    @PostConstruct
    public void cerrarCohorteAuto() {

        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteEntity == null)
            return;
        LocalDateTime fechaEspecifica = cohorteEntity.getFechaFin().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        long tiempoRestante = LocalDateTime.now().until(fechaEspecifica, ChronoUnit.SECONDS);

        executorService.schedule(() -> {
            cohorteEntity.setHabilitado(false);
            cohorteRepository.save(cohorteEntity);
            abrirCohorteAuto();
        }, tiempoRestante, TimeUnit.SECONDS);
    }

    @PostConstruct
    public void abrirCohorteAuto() {

        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteEntity != null)
            return;

        cohorteEntity = cohorteRepository.findCohorteWithClosestStartDate();
        if (cohorteEntity == null)
            return;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        LocalDateTime fechaEspecifica = cohorteEntity.getFechaInicio().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        long tiempoRestante = LocalDateTime.now().until(fechaEspecifica, ChronoUnit.SECONDS);
        cohorteEntity.setHabilitado(true);
        CohorteEntity finalCohorteEntity = cohorteEntity;
        executorService.schedule(() -> {
            cohorteRepository.save(finalCohorteEntity);
            cerrarCohorteAuto();
        }, tiempoRestante, TimeUnit.SECONDS);
    }

}
