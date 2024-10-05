package com.admision.maestrias.api.pam.repository;

import com.admision.maestrias.api.pam.entity.EstadoDocEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository que define los métodos para acceder al estado de los documentos del Aspirante.
 * @author Angel Yesid Duque Cruz
 */
public interface EstadoDocRepository extends JpaRepository<EstadoDocEntity, Integer> {
}
