package com.admision.maestrias.api.pam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.entity.EstadoEntity;

/**
 * Repository que define los métodos para acceder al estado del Aspirante.
 * @author Angel Yesid Duque Cruz, Gibson Arbey
 */
public interface EstadoRepository extends JpaRepository <EstadoEntity, Integer>{
    
    /**
     * Buscar estado por descripcion
     * @param descripcion contiene la descripcion del estado a buscar
     * @return un objeto EstadoEntity que corresponda la descripcion si lo encuentra, en caso contrario retorna null
     */
    EstadoEntity findByDescripcion(String descripcion);

    /**
     * Buscar estados por aspirante.
     *
     * @param aspirante el objeto AspiranteEntity para buscar los estados asociados
     * @return una lista de objetos EstadoEntity que corresponden al aspirante proporcionado,
     *         o una lista vacía si no se encuentran estados.
     */
    List<EstadoEntity> findByAspirante(AspiranteEntity aspirante);
}
