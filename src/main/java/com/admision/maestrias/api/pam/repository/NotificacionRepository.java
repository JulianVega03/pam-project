package com.admision.maestrias.api.pam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.entity.NotificacionEntity;
import java.util.List;


/**
 * Repository que define los métodos para acceder a las notificaciones.
 * @author Angel Gabriel García Rangel, Angel Yesid Duque Cruz, Gibson Arbey
 */
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Integer>{
    
    /**
     * Buscar notificaciones por aspirante.
     * @param aspirante el objeto AspiranteEntity para buscar las notificaciones asociadas
     * @return una lista de objetos NotificacionEntity que corresponden al aspirante proporcionado,
     *         o una lista vacía si no se encuentran notificaciones.
     */
    List<NotificacionEntity> findByAspirante(AspiranteEntity aspirante);
}
