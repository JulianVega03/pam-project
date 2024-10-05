package com.admision.maestrias.api.pam.repository;

import com.admision.maestrias.api.pam.entity.CohorteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Define los metodos para acceder a los datos del cohorte
 * @author Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Gibson Arbey, Juan Pablo Correa Tarazona
 */
public interface CohorteRepository extends JpaRepository<CohorteEntity, Integer> {
  
    /**
     * Buscar cohorte habilitada
     * @param habilitado que contiene el estado de la cohorte a buscar 
     * @return un objeto CohorteEntity que contiene la unica cohorte habilitado, en caso contrario retorna null 
     */
    CohorteEntity findCohorteByHabilitado(boolean habilitado);


    /**
     * Buscar cohorte con la fecha de inicio mayor a la actual
     * @return un objeto cohorteEntity que cumpla esa condicion, en caso contrario retorna null
     */
    @Query(value = "SELECT * FROM cohorte c WHERE c.fecha_inicio >= CURRENT_DATE ORDER BY c.fecha_inicio ASC LIMIT 1", nativeQuery = true)
    CohorteEntity findCohorteWithClosestStartDate();

     /**
     * Buscar cohortes entre un rango de fechas
     * @param fechaInicio que contiene el rango de inicio de las cohortes a buscar
     * @param fechaFin contiene el rango de fin de las cohortes a buscar 
     * @return un listado de CohorteEntity con todas las cohortes en ese rango de fechas 
     */
    @Query(value = "SELECT * FROM Cohorte c WHERE :fechaInicio <= c.fecha_fin AND :fechaFin >= c.fecha_inicio", nativeQuery = true)
    List<CohorteEntity> findCohortesInRange(Date fechaInicio, Date fechaFin);

    /**
     * Buscar cohortes por su fecha maxima de prueba
     * @param fecha_max_prueba contiene la fecha maxima para presentar la prueba
     * @return un obejeto CohorteEntity con la cohorte que tenga esa fecha, en caso contrario retorna null 
     */
    CohorteEntity findByFechaMaxPrueba(LocalDateTime fecha_max_prueba);

    /**
     * Buscar cohortes por id
     * @param id contiene el id asignado a la cohorte
     * @return un obejeto CohorteEntity con la cohorte que tenga esa id, en caso contrario retorna null 
     */
    Optional<CohorteEntity> findById(Integer id);
}
