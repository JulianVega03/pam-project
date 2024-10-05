package com.admision.maestrias.api.pam.repository;

import com.admision.maestrias.api.pam.shared.dto.DocumentoDTO;
import com.admision.maestrias.api.pam.shared.dto.S3FileInfo;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface que define los métodos para acceder al S3.
 * @author Julian Camilo Riveros Fonseca
 */
public interface S3Repository {

    /**
     * Obtener la lista de objetos en el bucket.
     * @return una lista de objetos DocumentoDTO que representa los objetos en el bucket.
     */
    List<DocumentoDTO> listObjectsInBucket();

    /**
     * Obtener el flujo de entrada del objeto en el bucket.
     * @param fileName el nombre del archivo a obtener
     * @return el flujo de entrada (S3ObjectInputStream) del objeto en el bucket
     * @throws IOException si ocurre un error al obtener el objeto
     */
    S3ObjectInputStream getObject( String fileName) throws IOException;

    /**
     * Descargar el archivo desde el bucket como un arreglo de bytes.
     * @param fileName el nombre del archivo a descargar
     * @return un arreglo de bytes que representa el contenido del archivo descargado
     * @throws IOException si ocurre un error al descargar el archivo
     */
    byte[] downloadFile(String fileName) throws IOException;

    /**
     * Eliminar el objeto con la clave especificada del bucket.
     * @param fileKey la clave (nombre) del archivo a eliminar
     * @return true si el objeto se eliminó correctamente, o false si no se pudo eliminar
     */
    boolean deleteObject (String fileKey);

    /**
     * Subir un archivo al bucket.
     * @param fileName el nombre del archivo en el bucket
     * @param fileObj el objeto File que representa el archivo a subir
     * @return un objeto ObjectMetadata que contiene información sobre el archivo subido
     */
    ObjectMetadata uploadFile(String fileName, File fileObj);

    /**
     * Crear una carpeta en el bucket con el nombre completo especificado.
     * @param nombreCompleto el nombre completo de la carpeta a crear
     * @return true si la carpeta se creó correctamente, o false si no se pudo crear
     */
    boolean crearCarpeta(String nombreCompleto);

    /**
     * Obtener las URLs de los archivos en la carpeta especificada del bucket.
     * @param folderName el nombre de la carpeta en el bucket
     * @return una lista de objetos S3FileInfo que representan la información de los archivos en la carpeta
     */
    List<S3FileInfo> getUrlsOfFilesInFolder(String folderName);

    /**
     * Obtener la URL del archivo con el nombre especificado en el bucket.
     * @param fileName el nombre del archivo
     * @return la URL del archivo en el bucket
     */
    String getFileUrl(String fileName);
}