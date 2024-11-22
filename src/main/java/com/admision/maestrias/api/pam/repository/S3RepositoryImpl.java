package com.admision.maestrias.api.pam.repository;

import com.admision.maestrias.api.pam.exceptions.DocumentNotFoundException;
import com.admision.maestrias.api.pam.shared.dto.DocumentoDTO;
import com.admision.maestrias.api.pam.shared.dto.S3FileInfo;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Implementación del repositorio de Amazon S3 que utiliza el cliente de Amazon S3 para interactuar con los objetos de S3.
 * @author Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Repository
public class S3RepositoryImpl implements S3Repository{

    @Autowired
    private AmazonS3 s3Client;

    private static final Logger log = LoggerFactory.getLogger(S3RepositoryImpl.class);
    private final String bucket = "admisionposgrados";

    /**
     * Obtiene una lista de DocumentoDTO de los objetos de un bucket de S3.
     * @return una lista de DocumentoDTO con la información de los objetos del bucket.
     */
    @Override
    public List<DocumentoDTO> listObjectsInBucket() {
        List<DocumentoDTO>  items =
                s3Client.listObjectsV2(bucket).getObjectSummaries().stream()
                        .parallel()
                        .map(S3ObjectSummary::getKey)
                        .map(key -> mapS3ToObject(bucket, key))
                        .collect(Collectors.toList());

        log.info("Found " + items.size() + " objects in the bucket " + bucket);
        return items;
    }

    /**
     * Mapea un objeto de S3 a un objeto de tipo DocumentoDTO.
     *
     * @param key clave del objeto en el bucket de S3.
     * @param s
     * @return un objeto de tipo DocumentoDTO con la información del objeto de S3.
     */
    private DocumentoDTO mapS3ToObject(String key, String s) {
        return DocumentoDTO.builder()
                .keyFile(key)
                .url(s3Client.getUrl(bucket, key))
                .build();
    }

    /**
     * Obtiene un objeto de S3.
     * @param fileName nombre del archivo en el bucket de S3.
     * @return un objeto de tipo S3ObjectInputStream con el contenido del archivo.
     * @throws IOException si ocurre un error al obtener el archivo de S3.
     */
    @Override
    public S3ObjectInputStream getObject(String fileName) throws IOException {
        if (!s3Client.doesBucketExist(bucket)) {
            log.error("No Bucket Found");
            return null;
        }
        try {
            S3Object s3object = s3Client.getObject(bucket, fileName);
            return s3object.getObjectContent();
        } catch (AmazonS3Exception e) {
            throw new DocumentNotFoundException("El archivo no existe!!");
        }
    }

    /**
     * Descarga un archivo de S3.
     * @param fileName nombre del archivo en el bucket de S3.
     * @return un array de bytes con el contenido del archivo.
     */
    @Override
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucket, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina un objeto del S3
     * @param fileKey nombre del archivo en el bucket de S3.
     */
    @Override
    public boolean deleteObject (String fileKey) {
        s3Client.deleteObject(bucket, fileKey);
        try {
            ObjectMetadata metadata = s3Client.getObjectMetadata(bucket, fileKey);
            return false;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * Subir un archivo al S3
     * @param fileName El nombre del archivo que se va a cargar en el bucket.
     * @param fileObj El objeto de tipo File que contiene el archivo a cargar en el bucket.
     * @return Un mensaje que indica que el archivo ha sido cargado correctamente en el bucket.
     */
    @Override
    public ObjectMetadata uploadFile(String fileName, File fileObj) {
        s3Client.putObject(new PutObjectRequest(bucket, fileName, fileObj));
        fileObj.delete();
        try {
            ObjectMetadata metadata = s3Client.getObjectMetadata(bucket, fileName);
            return metadata;
        } catch (AmazonS3Exception e) {
            throw e;
        }

    }

    /**
     * Comprobar si un archivo existe en el S3.
     *
     * @param fileName el nombre del archivo a comprobar
     * @return true si el archivo existe en Amazon S3, false si no existe
     * @throws AmazonS3Exception si ocurre un error al acceder a Amazon S3
     */
    public boolean comprobarArchivoEnS3(String fileName) {
        try {
            ObjectMetadata metadata = s3Client.getObjectMetadata(bucket, fileName);
            return true;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return false;
            } else {
                throw e;
            }
        }
    }


    /**
     * Crear una carpeta en el bucket con el nombre completo especificado.
     * @param nombreCompleto el nombre completo de la carpeta a crear
     * @return true si la carpeta se creó correctamente, o false si no se pudo crear
     */
    @Override
    public boolean crearCarpeta( String nombreCarpeta) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, nombreCarpeta + "/", emptyContent, metadata);
        try {
            s3Client.putObject(putObjectRequest);
            return true;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return  false;
            } else {
                throw e;
            }
        }
    }

    /**
     * Obtener las URLs de los archivos en la carpeta especificada del bucket.
     * @param folderName el nombre de la carpeta en el bucket
     * @return una lista de objetos S3FileInfo que representan la información de los archivos en la carpeta
     */
    @Override
    public List<S3FileInfo> getUrlsOfFilesInFolder(String folderName) {

        List<S3FileInfo> fileInfos = new ArrayList<>();

        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(folderName + "/");

        ListObjectsV2Result result;
        do {
            result = s3Client.listObjectsV2(listObjectsRequest);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                if (!objectSummary.getKey().endsWith("/")) {
                    S3FileInfo fileInfo = new S3FileInfo();

                    // Obtener la información del objeto
                    GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucket, objectSummary.getKey());
                    ObjectMetadata objectMetadata = s3Client.getObjectMetadata(metadataRequest);

                    // Obtener la URL firmada para el objeto
                    GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucket, objectSummary.getKey())
                            .withMethod(HttpMethod.GET)
                            .withExpiration(new Date(System.currentTimeMillis() + 3600000)); // Caducidad de 1 hora
                    URL url = s3Client.generatePresignedUrl(urlRequest);

                    // Establecer la información en el objeto S3FileInfo
                    fileInfo.setBucketName(bucket);
                    fileInfo.setFileName(objectSummary.getKey());
                    fileInfo.setContentLength(objectMetadata.getContentLength());
                    fileInfo.setContentType(objectMetadata.getContentType());
                    fileInfo.setLastModified(objectMetadata.getLastModified());
                    fileInfo.setUrl(url.toString());

                    fileInfos.add(fileInfo);
                }
            }

            listObjectsRequest.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return fileInfos;

    }

    /**
     * Obtener la URL del archivo con el nombre especificado en el bucket.
     * @param fileName el nombre del archivo
     * @return la URL del archivo en el bucket
     */
    public String getFileUrl(String fileName) {
        String url = s3Client.getUrl(bucket, fileName).toString();
        return url;
    }

}