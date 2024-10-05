package com.admision.maestrias.api.pam.repository;

import com.admision.maestrias.api.pam.entity.EstadoDocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.entity.DocumentoEntity;
import com.admision.maestrias.api.pam.entity.TipoDocumentoEntity;

import java.util.List;

/**
 * Repository que define los métodos para acceder a los documentos del
 * Aspirante.
 * @author Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Gibson Arbey, Juan Pablo Correa Tarazona, Miguel Angel Lara
 */
public interface DocumentoRepository extends JpaRepository<DocumentoEntity, Integer> {

    /**
     * Buscar documentos por aspirante
     * 
     * @param aspirante que contiene el aspirante al que se le van a buscar los
     *                  documentos
     * @return un listado con los documentos del aspirante
     */
    List<DocumentoEntity> findByAspirante(AspiranteEntity aspirante);

    /**
     * Buscar documentos por id de tipo
     * 
     * @param tipo_documento_id contiene el id del tipo de documento a buscar
     * @return un listado con los documentos que cumplan con el tipo a buscar
     */
    List<DocumentoEntity> findByDocumentoId(TipoDocumentoEntity tipo_documento_id);

    /**
     * Buscar documentos por tipo
     * 
     * @param tipo_documento contiene el tipo de documento a buscar
     * @return un listado con los documentos que cumplan con el tipo a buscar
     */
    List<DocumentoEntity> findByDocumento(TipoDocumentoEntity documento);

    /**
     * Buscar documentos por tipo
     * 
     * @param tipo_documento contiene el tipo de documento a buscar
     * @return un listado con los documentos que cumplan con el tipo a buscar
     */
    List<DocumentoEntity> findByAspiranteAndEstado(AspiranteEntity aspirante, EstadoDocEntity estado);

    /**
     * Buscar un objeto DocumentoEntity por aspirante y tipo de documento.
     *
     * @param idAspirante     el ID del aspirante para buscar el documento
     * @param idTipoDocumento el ID del tipo de documento para buscar el documento
     * @return un objeto DocumentoEntity que corresponde al aspirante y tipo de documento proporcionados,
     * o null si no se encuentra ningún documento.
     */
    @Query(value = "SELECT * FROM documento D where D.documento_id = :idtipo_documento and D.aspirante_id = :idAspirante", nativeQuery = true)
    DocumentoEntity findByAspiranteAndDocumento(@Param("idAspirante") Integer idAspirante,
            @Param("idtipo_documento") Integer idTipoDocumento);

    /**
     * Buscar documentos por estado y aspirante.
     *
     * @param idEstado       el ID del estado del documento
     * @param idAspirante    el ID del aspirante para buscar los documentos
     * @return una lista de objetos DocumentoEntity que corresponden al estado y aspirante proporcionados,
     *         o una lista vacía si no se encuentran documentos.
     */
    @Query(value = "SELECT * FROM documento D where D.estado_id = :idEstado and D.aspirante_id = :idAspirante", nativeQuery = true)
    List<DocumentoEntity> findDocumentosByEstado(@Param("idEstado") Integer idEstado,
            @Param("idAspirante") Integer idAspirante);

        /**
         * Contar la cantidad de documentos por aspirante.
         *
         * @param aspirante el aspirante para el cual contar los documentos
         * @return la cantidad de documentos asociados al aspirante
         */
        Integer countByAspirante(AspiranteEntity aspirante);
}
