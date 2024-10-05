package com.admision.maestrias.api.pam.service.interfaces;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.admision.maestrias.api.pam.shared.dto.UserDTO;


public interface UserServiceInterface extends UserDetailsService{
    
    public void createUser(UserDTO user);

    public List<UserDTO> getUserByRol(String rol);

    public void reestablecerContrasena(String email, String actualContraseña, String nuevaContraseña);

    public void createEncargado(UserDTO user);

    public void deleteEncargado(String email);

}
