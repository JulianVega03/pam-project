package com.admision.maestrias.api.pam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.admision.maestrias.api.pam.entity.RolEntity;

/**
 * Repository que define los métodos para acceder a los roles.
 * @author Juan Pablo Correa Tarazona, Gibson Arbey
 */
public interface RolRepository extends JpaRepository<RolEntity, Integer>{
    
    /**
     * Buscar rol por autoridad.
     *
     * @param authority la autoridad del rol a buscar
     * @return un objeto RolEntity que corresponde a la autoridad proporcionada,
     *         o null si no se encuentra ningún rol.
     */
    RolEntity findByAuthority(String authority);
}
