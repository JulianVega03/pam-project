package com.admision.maestrias.api.pam.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Clase AppProperties la creamos como Bean
 * nos permite tomar valores escritos en el archivo application.properties
 * @author Juan Pablo Correa Tarazona
 */
@Component
public class AppProperties {
    
    @Autowired
    private Environment env;

    public String getTokenSecret(){
        return env.getProperty("tokenSecret");
    }
}
