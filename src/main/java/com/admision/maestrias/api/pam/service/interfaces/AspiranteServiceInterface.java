package com.admision.maestrias.api.pam.service.interfaces;

import com.admision.maestrias.api.pam.models.responses.AspiranteCohorteResponse;
import com.admision.maestrias.api.pam.shared.dto.AspiranteDTO;
import com.admision.maestrias.api.pam.shared.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AspiranteServiceInterface {
  
    public AspiranteDTO crearAspirante(AspiranteDTO aspirante, String email);
    
    public List<AspiranteDTO> listarAspirantesCohorteActual();

    public UserDTO getUserByAspirante(Integer id);

    public void disableAspirante(String email);

    public void enableAspirante(String email);

    AspiranteCohorteResponse getAspiranteByUserEmail(String email);

    public AspiranteDTO getAspiranteByAspiranteId (Integer aspiranteId);
  
    public AspiranteDTO getAspiranteById(int id);

    public void habilitarFechaEntrevista(Integer id, LocalDateTime fecha_entrevista);

    public void calificarPruebaAspirante (int id, int calificacionPrueba);

    public void calificarEntrevistaAspirante (int id, int calificacionEntrevista);

    public void admitirAspirante(Integer aspiranteId);

    public List<AspiranteDTO> listarAdmitidos(Integer estadoId);

    public List<AspiranteDTO> obtenerAspirantesHistoricosCohorte(Integer cohorteId);

    public void rechazarAdmisionAspirante(Integer aspiranteId);
  
    public void calificarDocsIndivi(Integer aspiranteId, Integer puntajeCartas, Integer puntajeNotasPregrado, Double puntajePublicaciones, Double puntajeDistinciones, Double puntajeExperiencia);

    public void cambiarEsEgresado(Integer aspiranteId);
    
}
