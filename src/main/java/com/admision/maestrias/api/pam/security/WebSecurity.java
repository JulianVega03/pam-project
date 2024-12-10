package com.admision.maestrias.api.pam.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.admision.maestrias.api.pam.service.implementations.JWTService;
import com.admision.maestrias.api.pam.service.interfaces.UserServiceInterface;

/**
 * @author Juan Pablo Correa Tarazona
 */
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    UserServiceInterface userService;
    @Autowired
    JWTService jwtService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
            .antMatchers( "/users","/login", "/documentos/cargar", "/tiposDoc", "/users/reestablecer/email", "/report/user/**").permitAll()
            .antMatchers( "/users/**","/users/reestablecer", "/cohorte/**", "/aspirante/**", "/documentos/**", "/notificacion/**","/doc/**").permitAll()
            .anyRequest().authenticated().and()
            .addFilter(getAuthenticationFilter())
            .addFilter(new AuthorizationFilter(authenticationManager(), jwtService)).sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager(),jwtService);
        filter.setFilterProcessesUrl("/login");
        return filter;
    }
}