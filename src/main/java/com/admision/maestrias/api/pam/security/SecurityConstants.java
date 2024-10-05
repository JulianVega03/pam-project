package com.admision.maestrias.api.pam.security;

import com.admision.maestrias.api.pam.SpringApplicationContext;

/**
 * @author Juan Pablo Correa Tarazona
 */
public class SecurityConstants {
    public static final long EXPIRATION_DATE = 86400000;// 1 dia en milisegundos
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static String getTokenSecret() {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
