package com.admision.maestrias.api.pam.security;

import com.admision.maestrias.api.pam.models.requests.UserDetailsRequest;
import com.admision.maestrias.api.pam.service.implementations.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Juan Pablo Correa Tarazona, Julian Camilo Riveros Fonseca
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Intenta autenticar al usuario validando el email y la contraseña si las credenciales son validas pasa
     * a successfulAuthentication sino va a unsuccessfulAuthentication
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UserDetailsRequest userModel = new ObjectMapper().readValue(request.getInputStream(),UserDetailsRequest.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userModel.getEmail(), userModel.getPassword());

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Si las credenciales son válidas devuelve un token JWT el cual contiene email, rol del usuario y un tiempo
     * de expiración de 1 día
     */
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {

        String username = ((User)authentication.getPrincipal()).getUsername();

        String token = jwtService.create(authentication);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("mensaje", String.format("Hola %s, has iniciado sesion con exito!", username));
                    
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.setContentType("application/json");

    }

    /**
     * Si las credenciales son inválidas retorna un mensaje de error
     */
    @Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		Map<String, Object> body = new HashMap<String, Object>();
        if (failed instanceof BadCredentialsException)
            body.put("message", "Correo o contraseña incorrectos");
        else
            body.put("message", failed.getMessage());

		body.put("error", "Error de autenticación");
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}

}
