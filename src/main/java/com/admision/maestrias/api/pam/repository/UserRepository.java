package com.admision.maestrias.api.pam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.admision.maestrias.api.pam.entity.RolEntity;
import com.admision.maestrias.api.pam.entity.UserEntity;

/**
 * Repository que define los métodos para acceder a los usuarios.
 * @author Juan Pablo Correa Tarazona, Julian Camilo Riveros Fonseca, Gibson Arbey
 */
public interface UserRepository extends JpaRepository<UserEntity, Integer>{

    /**
     * Buscar usuario por correo electrónico.
     *
     * @param email el correo electrónico del usuario a buscar
     * @return un objeto UserEntity que corresponde al correo electrónico proporcionado,
     *         o null si no se encuentra ningún usuario.
     */
    UserEntity findByEmail(String email);

    /**
     * Buscar un usuario por su ID.
     * @param integer el ID del usuario a buscar
     * @return un objeto Optional que puede contener un UserEntity correspondiente al ID proporcionado,
     *         o puede estar vacío si no se encuentra ningún usuario.
     */
    @Override
    Optional<UserEntity> findById(Integer integer);

    /**
     * Buscar usuarios por rol.
     * @param rol el objeto RolEntity para buscar los usuarios asociados
     * @return una lista de objetos UserEntity que corresponden al rol proporcionado,
     *         o una lista vacía si no se encuentran usuarios.
     */
    List<UserEntity> findByRol(RolEntity rol);
    
}
