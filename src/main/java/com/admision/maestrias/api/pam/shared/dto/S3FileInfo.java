package com.admision.maestrias.api.pam.shared.dto;


import lombok.Data;

import java.util.Date;

@Data
public class S3FileInfo {
    private String bucketName;
    private String fileName;
    private long contentLength;
    private String contentType;
    private Date lastModified;
    private String url;
}
