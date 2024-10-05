package com.admision.maestrias.api.pam.service.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Esta interfaz define los métodos necesarios para guardar, buscar y recuperar documentos.
 */
public interface DocumetoServiceInterface {

    /**
     * Guarda un documento
     *
     * @param file El documento a guardar.
     * @throws Exception Si hay algún problema al guardar el documento.
     */
    public void save(MultipartFile file) throws Exception;

    /**
     * Carga archivos
     *
     * @param nombre El nombre del recurso a buscar.
     * @return El recurso encontrado.
     * @throws Exception Si el recurso no se encuentra o si hay algún problema al buscar el recurso.
     */
    public Resource load(String nombre) throws Exception;

    /**
     * Guarda una varios archivos a la vez.
     *
     * @param file La lista de documentos a guardar.
     * @throws Exception Si hay algún problema al guardar los documentos.
     */
    public void save(List<MultipartFile> file) throws Exception;

    /**
     * Subir todos los archivos
     *
     * @return El flujo de rutas que representa todos los documentos disponibles.
     * @throws Exception Si hay algún problema al buscar los documentos.
     */
    public Stream<Path> saveAll() throws Exception;



}