package com.admision.maestrias.api.pam.controller;

import com.admision.maestrias.api.pam.models.responses.AnyResponse;
import com.admision.maestrias.api.pam.models.responses.DocumentoResponse;
import com.admision.maestrias.api.pam.service.implementations.AspiranteService;
import com.admision.maestrias.api.pam.service.implementations.AwsService;
import com.admision.maestrias.api.pam.shared.dto.AspiranteDTO;
import com.admision.maestrias.api.pam.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Esta clase es un controlador REST para interactuar con los servicios de Amazon S3.
 * Está mapeado en la ruta "/documentos".
 * @author Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@RestController
@RequestMapping(value = "/documentos")
public class DocumentosController {

    @Autowired
    private AwsService awsService;

    @Autowired
    private AspiranteService aspiranteService;

    /**
     * Método GET que retorna la lista de archivos en un bucket de Amazon S3.
     * @param idAspirante ID del aspirante
     * @return Objeto ResponseEntity que contiene la lista de archivos y el estado HTTP OK.
     * @throws IOException Si ocurre algún error al obtener la lista de archivos.
     */
    @GetMapping("/listFiles/{idAspirante}")
    public ResponseEntity<List<DocumentoResponse>> listarDocumentosAspirante(@PathVariable Integer idAspirante) throws IOException {

        List<DocumentoResponse> documentoResponses = new ArrayList<>();
        HttpStatus status=HttpStatus.OK;
        try {
            documentoResponses =  awsService.getAspiranteFiles(idAspirante);
        } catch (Exception e) {
            status =  HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(documentoResponses, status);
    }

    /**
     * Método GET que permite descargar un archivo de Amazon S3.
     * @param tipoDocumento ID tipo de documento
     * @return Objeto ResponseEntity que contiene el archivo y los encabezados necesarios para su descarga.
     * @throws IOException Si ocurre algún error al descargar el archivo.
     */
    @GetMapping("/downloadFile")
    public ResponseEntity<ByteArrayResource> downloadS3File(@RequestParam(value = "tipoDocumento") String tipoDocumento)
            throws IOException {
        Logger logger = LoggerFactory.getLogger(DocumentosController.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        byte[] data = awsService.downloadFile(tipoDocumento, email);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + tipoDocumento + "\"")
                .body(resource);
    }

    /**
     * Método DELETE que permite eliminar un archivo de Amazon S3.
     * @param tipoDocumento Nombre del archivo que se desea eliminar.
     * @return Objeto ResponseEntity con el mensaje "File deleted" y el estado HTTP OK.
     */
    @DeleteMapping("/deleteObject")
    public ResponseEntity<AnyResponse> deleteFile(@RequestParam(value = "tipoDocumento") String tipoDocumento) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        if (awsService.deleteObject(tipoDocumento)){
            return new ResponseEntity<>(new AnyResponse("File deleted"), HttpStatus.OK);
        }else
            return new ResponseEntity<>(new AnyResponse("Ha ocurrido un error al eliminar el documento"),
                    HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * Método POST que sube un archivo a un bucket de AWS S3
     * @param tipoDocumento ruta del archivo en el bucket
     * @param file archivo a subir
     * @return ResponseEntity con mensaje de éxito y estado HTTP 200 OK
     */
    @Secured("ROLE_USUARIO")
    @PostMapping("/uploadFile/{tipoDocumento}")
    public ResponseEntity<AnyResponse> uploadFile(@PathVariable(value = "tipoDocumento") int tipoDocumento,
                                                  @RequestParam(value = "file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = Integer.parseInt(authentication.getDetails().toString());

        if (awsService.uploadFile(userId,tipoDocumento, file)){
            return new ResponseEntity<>(new AnyResponse("Documento subido con éxito"),
                                            HttpStatus.OK);
        }else
            return new ResponseEntity<>(new AnyResponse("Ha ocurrido un error al subir el documento"),
                                            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Método para descargar todos los archivos de un aspirante en una carpeta comprimida(ZIP)
     * @param id id del aspirante
     * @return ZIP de documentos del aspirante
     * @throws IOException si ocurre un error
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @GetMapping("/downloadFolder")
    public ResponseEntity<Resource> downloadS3Folder(@RequestParam Integer id) throws IOException {
        Logger logger = LoggerFactory.getLogger(DocumentosController.class);
        logger.info("Descargando carpeta");
        // Obtener lista de nombres de archivos en la carpeta del servicio
        List<DocumentoResponse> fileNames = new ArrayList<>();
        try {
            fileNames =  awsService.getAspiranteFiles(id);
        } catch (Exception e) {
            logger.error("Error al obtener la lista de archivos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        AspiranteDTO aspirante = aspiranteService.getAspiranteByAspiranteId(id);
        UserDTO user = aspiranteService.getUserByAspirante(id);
        // Crear archivo ZIP en memoria
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (DocumentoResponse fileName : fileNames) {
                // Obtener contenido de cada archivo en la carpeta del servicio
                if(fileName.getUrl()!=null) {
                    byte[] fileData = awsService.downloadFile(fileName.getKeyFile(), user.getEmail());
                    // Agregar archivo al ZIP
                    ZipEntry zipEntry = new ZipEntry(fileName.getDocumento().getNombre()+fileName.getFormato());
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(fileData);
                    zipOutputStream.closeEntry();
                }
            }
        } catch (IOException e){
            logger.error("Error al crear el archivo ZIP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // Convertir el archivo ZIP resultante en un recurso ByteArrayResource para enviarlo al front
        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        //pasamos el nombre del ZIP uwu
        headers.setContentDispositionFormData("attachment", aspirante+".zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity
                .ok().headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
