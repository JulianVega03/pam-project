package com.admision.maestrias.api.pam.service.implementations;

import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import com.admision.maestrias.api.pam.entity.DocumentoEntity;
import com.admision.maestrias.api.pam.models.responses.DocumentoResponse;
import com.admision.maestrias.api.pam.repository.*;
import com.admision.maestrias.api.pam.service.interfaces.AwsServiceInterface;
import com.amazonaws.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Implementación de AwsServiceInterface que interactúa con el S3.
 * @author Julian Camilo Riveros Fonseca, Juab Pablo Correa Tarazona
 */
@Service
public class AwsService implements AwsServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(AwsService.class);
    private final String bucket = "pamweb";

    @Autowired
    private S3Repository s3Repository;
    @Autowired
    private DocumentoRepository documentoRepository;
    @Autowired
    private AspiranteRepository aspiranteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EstadoDocRepository estadoDocRepository;
    @Autowired
    private TipoDocumentoRepository tipoDocRepository;
    @Autowired
    private EstadoRepository estadoRepository;

    /**
     * Obtiene una lista de los archivos en el bucket especificado.
     *
     * @return una lista de DocumentoDTO que representan los archivos en el bucket
     */
    public List<DocumentoResponse> getAspiranteFiles(int idAspirante) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(idAspirante);
        if(aspirante==null) {
            throw  new UsernameNotFoundException("No se encontró el usuario con id: " + idAspirante);
        }
        List<DocumentoResponse> documentoResponse = new ArrayList<>();

        for(DocumentoEntity doc : aspirante.get().getDocumentos()){
            DocumentoResponse d = new DocumentoResponse();
            BeanUtils.copyProperties(doc, d);
            documentoResponse.add(d);
        }
        return documentoResponse;
    }

    /**
     * Obtiene el contenido de un archivo en S3 como una cadena de texto.
     * @param fileName el nombre del archivo
     * @return el contenido del archivo como una cadena de texto
     * @throws IOException si ocurre un error al leer el archivo
     */
    public String getS3FileContent(String fileName) throws IOException {
        return getAsString(s3Repository.getObject(fileName));
    }

    /**
     * Obtiene el contenido de un InputStream como una cadena de texto.
     * @param is el InputStream del que se quiere obtener el contenido
     * @return el contenido del InputStream como una cadena de texto
     * @throws IOException si ocurre un error al leer el InputStream
     */
    private static String getAsString(InputStream is) throws IOException {
        if (is == null)
            return "";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StringUtils.UTF8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            is.close();
        }
        return sb.toString();
    }

    /**
     * Descarga un archivo del bucket especificado.
     * @param fileName el nombre del archivo a descargar
     * @return un arreglo de bytes que representa el contenido del archivo descargado
     * @throws IOException si ocurre un error al descargar el archivo
     */
    @Override
    public byte[] downloadFile(String fileName, String email) throws IOException {
        return s3Repository.downloadFile(fileName);
    }

    /**
     * Elimina un objeto del bucket especificado.
     * @param fileKey la clave del objeto a eliminar
     */
    @Override
    public boolean deleteObject(String fileKey) {
        return s3Repository.deleteObject("documentos/" + fileKey);
    }

    /**
     * Carga un archivo en S3.(Solo permite cargarse documentos con extensiones .jpg .pdf o .png)
     * @param tipoDocumento la ruta donde se guardará el archivo
     * @param file el archivo a cargar
     * @return una cadena que indica si la carga del archivo fue exitosa
     */
    @Override
    public boolean uploadFile(int userID, int tipoDocumento, MultipartFile file) {
        if (!esFormatoValido(tipoDocumento, file)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El archivo que intenta subir no tiene la extensión adecuada.");
        File fileObj = convertMultiPartFileToFile(file);
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findByUser_Id(userID);
        AspiranteEntity aspiranteEntity = aspirante.get();
        String filename = aspirante.get().getCohorte().getId()+"/"+aspirante.get().getId().toString()+"/"+tipoDocumento;
        try {
            s3Repository.uploadFile(filename, fileObj);
            DocumentoEntity documento = documentoRepository.findByAspiranteAndDocumento(aspirante.get().getId(), tipoDocumento);
            if(documento == null){
                documento = new DocumentoEntity();
            }else{
                if (documento.getEstado().getId() == 4) {
                    return false;
                }
            }            
            documento.setAspirante(aspirante.get());
            documento.setEstado(estadoDocRepository.findById(2).get());
            documento.setDocumento(tipoDocRepository.findById(tipoDocumento).get());
            documento.setUrl(s3Repository.getFileUrl(filename));
            documento.setKeyFile(filename);
            String nombreArchivo = file.getOriginalFilename();
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
            documento.setFormato(extension);
            aspirante.get().getDocumentos().add(documento);
            documentoRepository.save(documento);
            
            Integer nDocs = documentoRepository.findDocumentosByEstado(2, aspiranteEntity.getId()).size();
            if(nDocs == 10){//Cambiar el estado a documentos enviados
                aspiranteEntity.setEstado(estadoRepository.findById(3).get());
                aspiranteRepository.save(aspiranteEntity);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Convierte un objeto MultipartFile a un objeto File.
     * @param file el objeto MultipartFile a ser convertido.
     * @return el objeto File resultante.
     */
    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    public boolean crearCarpetaAspirante(String nombreCarpeta){
        return s3Repository.crearCarpeta(nombreCarpeta);
    }

    /**
     * Valida si el archivo enviado cumple la extensión que debería ser según el tipo de archivo
     * @param tipoDocumento tipo de documento ejemplo: 1: foto, 2: hoja de vida
     * @param file archivo a subir
     * @return True si es válido
     */
    private boolean esFormatoValido(int tipoDocumento, MultipartFile file){
        String nombreArchivo = file.getOriginalFilename();
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);

        //si el tipo de documento es Foto 3x4
        if(tipoDocumento==1){
            return  esFormatoImagenValido(extension);
        }else if(tipoDocumento==7){//es firma digitalizada
            return esFormatoPDFValido(extension) || !esFormatoImagenValido(extension);
        }else{//cualquier otro documento
            return esFormatoPDFValido(extension);
        }
    }

    /**
     * valida si una extensión de tipo imagen es valida según los formatos aceptados
     * @param extension String con extensión del archivo
     * @return True si es valida
     */
    private boolean esFormatoImagenValido(String extension) {
        return extension != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("png"));
    }

    /**
     * valida si una extensión de tipo pdf es valida
     * @param extension String con extensión del archivo
     * @return True si es valida
     */
    private boolean esFormatoPDFValido(String extension) {
        return extension != null && extension.equalsIgnoreCase("pdf");
    }
}
