package com.admision.maestrias.api.pam.service.interfaces;

import com.admision.maestrias.api.pam.models.responses.DocumentoResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface AwsServiceInterface {

    String getS3FileContent(String fileName) throws IOException;

    List<DocumentoResponse> getAspiranteFiles(int idAspirante) throws IOException;

    byte[] downloadFile(String fileName,String email) throws IOException;

    boolean deleteObject (String fileKey);

    boolean uploadFile(int userId, int filePath, MultipartFile file);
}
