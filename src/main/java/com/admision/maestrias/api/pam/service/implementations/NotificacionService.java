package com.admision.maestrias.api.pam.service.implementations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.admision.maestrias.api.pam.entity.CohorteEntity;
import com.admision.maestrias.api.pam.repository.CohorteRepository;
import com.admision.maestrias.api.pam.repository.NotificacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.entity.NotificacionEntity;
import com.admision.maestrias.api.pam.repository.AspiranteRepository;
import com.admision.maestrias.api.pam.service.interfaces.NotificacionServiceInterface;
import com.admision.maestrias.api.pam.shared.dto.NotificacionDTO;

import javax.persistence.EntityNotFoundException;

/**
 * @author Juan Pablo Correa Tarazona, Angel Yesid Duque Cruz
 */
@Service
public class NotificacionService implements NotificacionServiceInterface {

    @Autowired
    private AspiranteRepository aspiranteRepository;

    @Autowired
    public CohorteRepository cohorteRepository;
    
    @Autowired
    public NotificacionRepository notificacionRepository;

    @Autowired
    public EmailService emailService;


    /**
     * Método para listar las notificaciones de un aspirante.
     *
     * @param aspiranteId el ID del aspirante del cual se obtendrán las
     *                    notificaciones
     * @return una lista de objetos NotificacionDTO que contiene la información de
     *         las notificaciones
     */

    @Override
    public List<NotificacionDTO> listarNotificaciones(Integer aspiranteId) {
        AspiranteEntity aspiranteEntity = aspiranteRepository.findById(aspiranteId).orElse(null);

        if (aspiranteEntity != null) {
            List<NotificacionEntity> notificacionEntities = new ArrayList<>(aspiranteEntity.getNotificaciones());
            List<NotificacionDTO> notificacionesDTO = new ArrayList<>();

            for (NotificacionEntity notificacionEntity : notificacionEntities) {
                NotificacionDTO notificacionDTO = new NotificacionDTO();
                notificacionDTO.setId(notificacionEntity.getId());
                notificacionDTO.setEnunciado(notificacionEntity.getEnunciado());
                notificacionDTO.setEstado(notificacionEntity.getEstado());
                notificacionDTO.setFecha_envio(notificacionEntity.getFecha_envio());

                notificacionesDTO.add(notificacionDTO);
            }

            return notificacionesDTO.stream()
                                    .sorted(Comparator.comparingInt(NotificacionDTO::getId).reversed())
                                    .collect(Collectors.toList());
        }else
            throw new EntityNotFoundException("No se encontró el aspirante con el ID: " + aspiranteId);
    }

    public void crearNotificacion(String mensaje, Integer aspiranteId) {
        try{
            NotificacionEntity notificacion = new NotificacionEntity();
            notificacion.setEnunciado(mensaje);
            notificacion.setEstado(false);
            notificacion.setFecha_envio(new java.sql.Date(System.currentTimeMillis()));
            if(aspiranteId>0){
                AspiranteEntity aspirante = aspiranteRepository.findById(aspiranteId).orElse(null);
                if(aspirante != null){
                    notificacion.setAspirante(aspirante);
                    aspirante.getNotificaciones().add(notificacion);
                    aspiranteRepository.save(aspirante);
                    emailService.sendListEmail(aspirante.getCorreoPersonal(), "Notificación de su proceso de admisión", mensaje);
                }else {
                    throw new EntityNotFoundException("No se encontró el aspirante con el ID: " + aspiranteId);
                }
            }else{
                CohorteEntity cohorte = cohorteRepository.findCohorteByHabilitado(true);
                List<AspiranteEntity> aspirantes = cohorte.getAspirantes();
                for (AspiranteEntity aspirante: aspirantes) {
                    notificacion.setAspirante(aspirante);
                    aspirante.getNotificaciones().add(notificacion);
                    emailService.sendListEmail(aspirante.getCorreoPersonal(), "Notificación de su proceso de admisión", mensaje);
                    aspiranteRepository.save(aspirante);
                }
            }
        }catch (Exception e){
            throw new EntityNotFoundException("Error al crear la notificación");
        }
    }

    /**
     * Método para marcar todas la notificaciones como leidas después de que se listen uwu. 
     */
    @Override
    public void marcarComoLeido(AspiranteEntity aspiranteEntity) {
       List<NotificacionEntity> notificacionEntities = notificacionRepository.findByAspirante(aspiranteEntity);
        for(NotificacionEntity n: notificacionEntities){
            n.setEstado(true); //Se cambia el estado
            notificacionRepository.save(n); // se actualiza en la bd xd
        }
    }

}
