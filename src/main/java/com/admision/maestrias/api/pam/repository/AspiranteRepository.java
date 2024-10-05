package com.admision.maestrias.api.pam.repository;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.entity.CohorteEntity;
import com.admision.maestrias.api.pam.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


/**
 * Repository que define los métodos para acceder a los datos del Aspirante.
 * @author Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Gibson Arbey, Juan Pablo Correa Tarazona
 */
public interface AspiranteRepository extends JpaRepository<AspiranteEntity, Integer> {

    /**
     * Buscar un aspirante por su id de usuario
     * @param user_id contiene el id asignado al usuario
     * @return un objeto AspiranteEntity que contiene al aspiante si lo encontro, en caso contrario retorna null 
     */
    Optional<AspiranteEntity> findByUser_Id(Integer user_id);

    /**
     * Buscar un aspirante por su id
     * @param id contiene el id asignado al aspirante
     * @return un objeto AspiranteEntity que contiene al aspiante si lo encontro, en caso contrario retorna null
     */
    Optional<AspiranteEntity> findById(Integer id);

    /**
     * Buscar un aspirante por su usuario
     * @param user un objeto usuario
     * @return un objeto AspiranteEntity que contiene al aspiante si lo encontro, en caso contrario retorna null
     */
    AspiranteEntity findByUser(UserEntity user);

    /**
     * Buscar un aspirante por su correo personal
     * @param correoPersonal contiene el correoPersonal del aspirante
     * @return un objeto AspiranteEntity que contiene al aspiante si lo encontro, en caso contrario retorna null
     */
    AspiranteEntity findByCorreoPersonal(String correoPersonal);
    

    /**
     * Listar los aspirantes inscriptos a una cohorte
     * @param cohorte un objeto CohorteEntity
     * @return un listado de los aspirantes inscriptos a esa cohorte
     */
    List<AspiranteEntity> findByCohorte(CohorteEntity cohorte);

    Optional<AspiranteEntity> findByUser_Email(String email);
    
}
