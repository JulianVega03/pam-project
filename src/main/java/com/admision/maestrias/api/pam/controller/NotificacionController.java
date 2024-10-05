package com.admision.maestrias.api.pam.controller;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.models.responses.AnyResponse;
import com.admision.maestrias.api.pam.models.responses.NotificacionResponse;
import com.admision.maestrias.api.pam.repository.AspiranteRepository;

import com.admision.maestrias.api.pam.service.implementations.NotificacionService;
import com.admision.maestrias.api.pam.shared.dto.NotificacionDTO;

/**
 * @author Julian Camilo Riveros Fonseca, Angel Yesid Duque Cruz, Juan Pablo Correa Tarazona
 */
@RestController
@RequestMapping("/notificacion")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private AspiranteRepository aspiranteRepository;

    /**
     * Endpoint para listar las notificaciones de un aspirante y devolverlas como
     * respuesta.
     * 
     * @return una lista de objetos NotificacionResponse que contiene la información
     *         de las notificaciones
     */
   @Secured("ROLE_USUARIO")
    @GetMapping("/listar")
    public List<NotificacionResponse> listarNotificacionesAspirante() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        try {
            Optional<AspiranteEntity> aspirante = aspiranteRepository.findByUser_Email(email);
            if (!aspirante.isPresent()) {
                throw new UsernameNotFoundException("No existe ningún aspirante asociado al email");
            }
            AspiranteEntity aspiranteEntity = aspirante.get();
            List<NotificacionDTO> notificacionesDTO = notificacionService.listarNotificaciones(aspiranteEntity.getId());
            List<NotificacionResponse> notificacionesResponse = new ArrayList<>();

            for (NotificacionDTO notificacionDTO : notificacionesDTO) {
                NotificacionResponse notificacionResponse = new NotificacionResponse();
                notificacionResponse.setEnunciado(notificacionDTO.getEnunciado());
                notificacionResponse.setEstado(notificacionDTO.getEstado());
                notificacionResponse.setFecha_envio(notificacionDTO.getFecha_envio());

                notificacionesResponse.add(notificacionResponse);
            }
            return notificacionesResponse;

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No se encontraron notificaciones del aspirante");
        }

    }

    

    /**
     * Marca las notificaciones de un usuario como leídas.
     * 
     * @return Una respuesta de tipo AnyResponse.
     * @throws NoSuchElementException si no se encuentra un aspirante asociado al
     *                                usuario.
     */
    @Secured("ROLE_USUARIO")
    @GetMapping("/checkRead")
    public ResponseEntity<AnyResponse> marcarNotificaciones() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findByUser_Email(email);
        if (!aspirante.isPresent()) {
            throw new UsernameNotFoundException("No existe ningún aspirante asociado al email");
        }
        AspiranteEntity aspiranteEntity = aspirante.get();

        notificacionService.marcarComoLeido(aspiranteEntity);
        return ResponseEntity.ok(new AnyResponse("Notificacion leida correctamente"));


    }

    /**
     * Metodo para formatear la fecha
     * @param fecha
     * @return
     */
    private String formatearFecha(LocalDateTime fecha) {
        Locale locale = Locale.getDefault();
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, locale);
        int dia = fecha.getDayOfMonth();
        int anio = fecha.getYear();
        int hora = fecha.getHour();
        int minuto = fecha.getMinute();
        int segundo = fecha.getSecond();

        return diaSemana + " " + dia + " de " + mes + " de " + anio + " a las " + hora + ":" + minuto + ":" + segundo;
    }
}
