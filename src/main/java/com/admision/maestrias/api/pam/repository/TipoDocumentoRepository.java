package com.admision.maestrias.api.pam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.admision.maestrias.api.pam.entity.TipoDocumentoEntity;

import java.util.Optional;

/**
 * Repository que define los métodos para acceder al tipo de documento.
 * @author Angel Yesid Duque Cruz, Miguel Angel Lara, Gibson Arbey
 */
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumentoEntity, Integer>{
    
    /**
     * Buscar tipo de documento por nombre.
     * @param nombre el nombre del tipo de documento a buscar
     * @return un objeto TipoDocumentoEntity que corresponde al nombre proporcionado,
     *         o null si no se encuentra ningún tipo de documento.
     */
    TipoDocumentoEntity findByNombre(String nombre);

    /**
     * Buscar tipo de documento por ID.
     * @param id el ID del tipo de documento a buscar
     * @return un objeto Optional que puede contener un TipoDocumentoEntity correspondiente al ID proporcionado,
     *         o puede estar vacío si no se encuentra ningún tipo de documento.
     */
    Optional<TipoDocumentoEntity> findById(Integer id);

    
}
