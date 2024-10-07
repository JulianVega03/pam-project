package com.admision.maestrias.api.pam.controller;

import com.admision.maestrias.api.pam.models.requests.ReestablecerRequest;
import com.admision.maestrias.api.pam.models.requests.UserDetailsRequest;
import com.admision.maestrias.api.pam.models.responses.AnyResponse;
import com.admision.maestrias.api.pam.models.responses.UserResponse;
import com.admision.maestrias.api.pam.service.implementations.EmailService;
import com.admision.maestrias.api.pam.service.implementations.JWTService;
import com.admision.maestrias.api.pam.service.implementations.UserService;
import com.admision.maestrias.api.pam.shared.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Juan Pablo Correa Tarazona
 */
@RestController
@RequestMapping("/users")

public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    JWTService jwtService;


    /**
     * Endpoint para registrar usuarios con el rol aspirante
     * @param userDetails Detalles del usuario a registrar
     * @return Respuesta dependiendo del exito de la operación
     */
    @PostMapping
    public ResponseEntity<AnyResponse> createUser(@RequestBody @Valid UserDetailsRequest userDetails) {
        
        UserDTO userDto = new UserDTO();

        BeanUtils.copyProperties(userDetails, userDto);
        try {
            userService.createUser(userDto);
            AnyResponse response = new AnyResponse("Te has registrado con exito, por favor revisa tu correo para confirmar tu cuenta");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw e;
        }

    }

    @GetMapping("/confirmar/{token}")
    public String confirmarCuenta(@PathVariable String token){

        return userService.confirmarCuenta(token);

    }

    /**
     * Endpoint para creación de usuarios con el rol de encargado
     * Solo está permitido para el rol de administrador.
     *
     * @param userDetails contiene los detalles del encargado
     * @return mensaje informativo
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/encargado")
    public ResponseEntity<AnyResponse> createEncargado(@RequestBody @Valid UserDetailsRequest userDetails) {

        UserDTO userDto = new UserDTO();
        BeanUtils.copyProperties(userDetails, userDto);
        userService.createEncargado(userDto);
        AnyResponse response = new AnyResponse("Te has registrado con exito!!!");
        return ResponseEntity.ok().body(response);
    }

    /**
     * Endpoint para obtener los usuarios segun el rol
     * Solo está permitido para el rol de administrador.
     *
     * @param rol rol del usuario
     * @return un listado con los usuarios
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/{rol}")
    public List<UserResponse> getUsersByRol(@PathVariable String rol) {
        List<UserResponse> response = new ArrayList<>();

        List<UserDTO> userDtos = userService.getUserByRol(rol);

        for (UserDTO userDto : userDtos) {
            UserResponse userRes = new UserResponse();
            BeanUtils.copyProperties(userDto, userRes);
            response.add(userRes);
        }

        return response;
    }
    
     /**
     * Endpoint Reestablecer contraseña
     * Solo está permitido para los roles de administrador y encargado.
     *
     * @param reestablecer que contiene la actual y la nueva contraseña
     * @return un mensaje informativo sobre el exito del endpoint
     */
    @Secured({ "ROLE_USUARIO", "ROLE_ENCARGADO" })
    @PostMapping("/reestablecer")
    public ResponseEntity<AnyResponse> reestablecerContrasena(@RequestBody @Valid ReestablecerRequest reestablecer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        userService.reestablecerContrasena(email, reestablecer.getActualContraseña(), reestablecer.getNuevaContraseña());

        return ResponseEntity.ok().body(new AnyResponse("Contraseña restablecida con éxito!"));
    }


    /**
     * Envía un correo electrónico para restablecer la contraseña del usuario con el
     * correo electrónico proporcionado.
     * 
     * @param email el correo electrónico del usuario.
     * @return Un objeto AnyResponse que contiene el mensaje de éxito o error de la
     *         operación.
     */
    @PostMapping("/reestablecer/email")
    public ResponseEntity<AnyResponse> emailContrasena(@RequestParam String email) {

        userService.emailContrasena(email);;

        return ResponseEntity.ok().body(new AnyResponse("Se ha enviado un correo electrónico para restablecer la contraseña."));
    }

    /**
     * Endpoint para eliminación por email de usuarios con el rol de encargado
     * Solo está permitido para el rol de administrador.
     *
     * @param email el correo electrónico del usuario.
     * @return mensaje informativo
    */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/eliminar/{email}")
    public ResponseEntity<AnyResponse> deleteEncargado(@PathVariable String email) {

         userService.deleteEncargado(email);
         return ResponseEntity.ok().body(new AnyResponse("Encargado eliminado con exito!!!"));
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

}
