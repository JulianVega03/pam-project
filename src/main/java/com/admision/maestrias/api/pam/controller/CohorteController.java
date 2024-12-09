package com.admision.maestrias.api.pam.controller;

import com.admision.maestrias.api.pam.models.requests.CohorteRequest;
import com.admision.maestrias.api.pam.models.responses.AnyResponse;
import com.admision.maestrias.api.pam.service.implementations.CohorteService;
import com.admision.maestrias.api.pam.shared.dto.CohorteDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author Angel Yesid Duque Cruz, Juan Pablo Correa Tarazona, Julian Camilo Riveros Fonseca, Ingrid FLorez, Gibson Arbey
 */
@RestController
@RequestMapping("/cohorte")
public class CohorteController {

    @Autowired
    CohorteService cohorteService;

    /**
     * Endpoint Abrir Cohorte
     * Abre una cohorte nueva en el sistema.
     * 
     * @param cohorteRequest que contiene los datos de la cohorte a abrir.
     * @return Un mensaje que indica si se abrió correctamente la cohorte.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/abrir")
    public ResponseEntity<AnyResponse> abrirCohorte(@RequestBody @Valid CohorteRequest cohorteRequest) {
        CohorteDTO cohorteDTO = new CohorteDTO();
        BeanUtils.copyProperties(cohorteRequest, cohorteDTO);

        cohorteService.abrirCohorte(cohorteDTO);

        return ResponseEntity.ok(new AnyResponse("Se ha abierto la cohorte correctamente"));
    }

    /**
     * Endpoint Cerrar Cohorte
     * Cierra la cohorte actual en el sistema.
     * 
     * @return Un mensaje que indica si se cerró correctamente la cohorte.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/cerrar")
    public ResponseEntity<AnyResponse> cerrarCohorte() {
        cohorteService.cerrarCohorte();
        return ResponseEntity.ok( new AnyResponse("Se ha cerrado la cohorte correctamente"));
    }

    /**
     * Endpoint que amplía la fecha de finalización de un cohorte existente.
     *
     * @param nuevaFechaFin La nueva fecha de finalización del cohorte.
     * @return Una instancia de {@link AnyResponse} que indica si se amplió la fecha
     *         del cohorte correctamente o si ocurrió un error.
     * @throws ParseException
     * @throws IllegalArgumentException Si la nueva fecha de finalización no es
     *                                  posterior a la fecha de finalización actual
     *                                  del cohorte.
     * @throws RuntimeException         Si no hay un cohorte actualmente habilitado.
     */
    @PutMapping("/fechaFin")
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    public ResponseEntity<AnyResponse> ampliarFechaFinCohorte(
            @RequestParam("nuevaFechaFin") String nuevaFechaFin) throws ParseException {
        
        Date date=new SimpleDateFormat("yyyy-MM-dd").parse(nuevaFechaFin);  
        cohorteService.ampliarFechaFinCohorte(date);
        return ResponseEntity.ok( new AnyResponse("Se ha ampliado la fecha del cohorte correctamente"));
    }

    /**
     * Endpoint Comprobar Cohorte
     * Comprueba si hay una cohorte abierta en el sistema y devuelve la información
     * de la cohorte actual.
     * 
     * @return un dto de la cohorte
     */
    @GetMapping("/abierto")
    public CohorteDTO comprobarCohorte() {
        return cohorteService.comprobarCohorte();
    }

    /**
     * Endpoint Listar Cohortes
     * Retorna una lista de objetos que representan todas las cohortes en el sistema
     * (Historico de cohortes).
     * Requiere rol de administrador para acceder a este endpoint.
     *
     * @return una lista con los cohorte DTOs
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @GetMapping
    public List<CohorteDTO> listarCohorte() {
        return cohorteService.listarCohorte();
    }

    /**
     * Endpoint guardar Enlace
     * Este metodo guarda el enlace de la entrevista y retorna toda la información
     * de la cohorte
     * junto con el enlace de la entrevista
     * Requiere rol de administrador para acceder a este endpoint.
     *
     * @param enlace contiene el enlace de la entrevista.
     * @return un objeto de la cohorteDTO que contiene los datos de la cohorte junto
     *         con el enlace de la entrevista
     *
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @PostMapping("/entrevistaEnlace")
    public ResponseEntity<AnyResponse> guardarEnlace(@RequestParam String enlace) {
        cohorteService.habilitarEnlace(enlace);
        return ResponseEntity.ok( new AnyResponse("El enlace ha sido guardado exitosamente."));
    }

    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @PostMapping("/entrevistaEnlace2")
    public ResponseEntity<AnyResponse> guardarEnlace2(@RequestParam String enlace) {
        cohorteService.habilitarEnlace2(enlace);
        return ResponseEntity.ok( new AnyResponse("El enlace ha sido guardado exitosamente."));
    }

    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @PostMapping("/entrevistaEnlace3")
    public ResponseEntity<AnyResponse> guardarEnlace3(@RequestParam String enlace) {
        cohorteService.habilitarEnlace3(enlace);
        return ResponseEntity.ok( new AnyResponse("El enlace ha sido guardado exitosamente."));
    }

    /**
     * Habilita la prueba para la cohorte abierta como administrador.
     * Requiere rol de administrador para acceder a este endpoint.
     *
     * @param enlace El objeto PruebaRequest que contiene los datos de la prueba a
     *               habilitar.
     * @return El objeto CohorteResponse con los datos de la cohorte habilitada para
     *         la prueba.
     */
    @Secured({ "ROLE_ADMIN", "ROLE_ENCARGADO" })
    @PostMapping("/prueba")
    public ResponseEntity<AnyResponse> habilitarPrueba(@RequestParam String enlace, @RequestParam String fecha_prueba) {
        LocalDateTime fechaPrueba = null;
        try {
            fechaPrueba = LocalDateTime.parse(fecha_prueba, DateTimeFormatter.ISO_ZONED_DATE_TIME);// Ejemplo formato fecha: 2021-05-20T00:00:00.000Z
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new AnyResponse("La fecha de la prueba no tiene el formato correcto"));
        }
        cohorteService.habilitarPrueba(enlace, fechaPrueba);
        return ResponseEntity.ok( new AnyResponse("Se ha habilitado la prueba correctamente"));
    }

}