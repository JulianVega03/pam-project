package com.admision.maestrias.api.pam.service.interfaces;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.admision.maestrias.api.pam.shared.dto.CohorteDTO;

public interface CohorteServiceInterface {

    public void abrirCohorte(CohorteDTO cohorte);

    public void cerrarCohorte();

    public CohorteDTO comprobarCohorte();

    public List<CohorteDTO> listarCohorte();

    public void habilitarEnlace(String enlace);

     public CohorteDTO habilitarPrueba(String enlace , LocalDateTime fechaMaxPrueba);

    public void ampliarFechaFinCohorte(Date nuevaFechaFin);

}
