package com.admision.maestrias.api.pam.service.interfaces;

import java.util.List;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.shared.dto.NotificacionDTO;

public interface NotificacionServiceInterface {

    List<NotificacionDTO> listarNotificaciones(Integer aspiranteId);

    void crearNotificacion(String mensaje, Integer aspiranteId);

    void marcarComoLeido(AspiranteEntity aspiranteEntity);
}
