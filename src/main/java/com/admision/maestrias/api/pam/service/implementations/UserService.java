package com.admision.maestrias.api.pam.service.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.admision.maestrias.api.pam.entity.RolEntity;
import com.admision.maestrias.api.pam.entity.UserEntity;
import com.admision.maestrias.api.pam.exceptions.EmailExistsException;
import com.admision.maestrias.api.pam.repository.RolRepository;
import com.admision.maestrias.api.pam.repository.UserRepository;
import com.admision.maestrias.api.pam.service.interfaces.UserServiceInterface;
import com.admision.maestrias.api.pam.shared.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.persistence.EntityNotFoundException;

/**
 * @author Juan Pablo Correa Tarazona, Angel Yesid Duque Cruz, Miguel Angel Lara, Julian Camilo Riveros Fonseca
 */
@Service("userService")
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired	
    private CohorteService cohorteService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private String appBaseUrl;

    /**
     * Creación de usuarios que se registran al sistema
     * 
     * @param user UserDTO con los datos del usuario a crear
     * @return usuario creado
     */
    @Override
    public void createUser(UserDTO user) {

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new EmailExistsException("Ya existe una cuenta con el correo electrónico proporcionado");

        if (user.getPassword().equals(user.getEmail()))
            throw new IllegalArgumentException("La contraseña no puede ser igual al correo electrónico");
        
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setRol(rolRepository.findByAuthority("ROLE_USUARIO"));
        userEntity.setCorreoConfirmado(false);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        String tokenUsuario = jwtService.generarToken(storedUserDetails);

        emailService.sendListEmail(userEntity.getEmail(), "Ingrese al siguiente enlace para confirmar su cuenta", generarEnlaceConfirmacion(tokenUsuario));

    }

    public String confirmarCuenta(String token){
        String html;
        try {
            int usuarioId = jwtService.getId2(token);
            UserEntity usuario = userRepository.findById(usuarioId).orElse(null);

            if (usuario != null) {
                if(usuario.isCorreoConfirmado()){
                    html = emailService.getHtml("Correo ya confirmado","");
                    return html;
                }
                usuario.setCorreoConfirmado(true);
                userRepository.save(usuario);
                html = emailService.getHtml("Cuenta creada y correo confirmado exitosamente.","");
                return html;
            } else {
                html = emailService.getHtml("Token de confirmación inválido.","");
                return html;
            }
        } catch (Exception e) {
            html = emailService.getHtml("Token de confirmación inválido.","");
            return html;
        }
    }

    /**
     * Creación de usuario con el rol de encargado, verifica que no exista ya un
     * usuario
     * registrado con el mismo correo
     * 
     * @param user Objeto con todos los datos del usuario a guardar.
     * @return UserDTO Objeto con los datos del usuario despues de guardar en la
     *         base de datos.
     * @throws EmailExistsException si ya hay un usuario registrado con el mismo
     *                              correo
     */
    @Override
    public void createEncargado(UserDTO user) {

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new EmailExistsException("El correo electronico ya existe");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        try {
            userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userEntity.setRol(rolRepository.findByAuthority("ROLE_ENCARGADO"));
            userEntity.setCorreoConfirmado(true);

            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        if(userEntity.getRol().getId() == 3 && cohorteService.comprobarCohorte() == null){
            throw new RuntimeException("No hay cohorte Abierto");
        }
        if(!userEntity.isCorreoConfirmado()){
            throw new RuntimeException("Correo no confirmado");
        }
        
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (userEntity.getRol() != null) {
            authorities.add(new SimpleGrantedAuthority(userEntity.getRol().getAuthority()));
        } else {
            throw new UsernameNotFoundException("Error en el Login: usuario '" + email + "' no tiene roles asignados!");
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), authorities);
    }

    /**
     * Obtener listado de usuarios por rol.
     * ordena los usuarios de cada rol por su email
     * @param rol rol de los usuarios que se buscaran.
     * @return List<UserDTO> Lista de usuarios que tengan el rol dado.
     */
    @Override
    public List<UserDTO> getUserByRol(String rol) {

        List<UserDTO> users = new ArrayList<>();
        List<UserEntity> userEntities = new ArrayList<>();
        try {
            RolEntity rolEntity = rolRepository.findByAuthority(rol);
            userEntities = userRepository.findByRol(rolEntity);
        } catch (Exception e) {
            throw new EntityNotFoundException("No existe el rol seleccionado");
        }

        for (UserEntity userEntity : userEntities) {
            UserDTO userDto = new UserDTO();
            BeanUtils.copyProperties(userEntity, userDto);
            users.add(userDto);
        }

        return users;
    }

    /**
     * Reestablecer contraseña de cualquier usuario.
     * 
     * @param email      email del usuario al que se le asignara la nueva
     *                   contraseña.
     * @param actualContraseña actual contraseña del usuario.
     * @param nuevaContraseña nueva contraseña a asignar.
     * @return boolean Refleja si la operacion tuvo exito.
     */
    @Override
    public void reestablecerContrasena(String email, String actualContraseña, String nuevaContraseña) {

        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("No existe el usuario con el correo electrónico proporcionado");
        }
        if (nuevaContraseña.equals(user.getEmail())){
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual al correo electrónico");
        }

        if (!bCryptPasswordEncoder.matches(actualContraseña, user.getEncryptedPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        user.setEncryptedPassword(bCryptPasswordEncoder.encode(nuevaContraseña));

        user = userRepository.save(user);

    }
    /**
     * Establece contraseña temporal aleatoria al usuario y la envia a traves de
     * correo electronico.
     * 
     * @param email email del usuario al que se le asignara la contraseña.
     * @return boolean Refleja si la operacion tuvo exito.
     */
    public void emailContrasena(String email) {

            UserEntity user = userRepository.findByEmail(email);

            if (user == null) {
                throw new UsernameNotFoundException("No existe el usuario con el correo electrónico proporcionado");
            }

            String tempcontrasena = generarContrasena(10);
            user.setEncryptedPassword(bCryptPasswordEncoder.encode(tempcontrasena));
            try {
                userRepository.save(user);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el usuario");
            }
            emailService.sendListEmail(email, "Contraseña de Recuperación" ,tempcontrasena);

    }

    /**
     * Eliminar encargado
     * 
     * @param email El correo electrónico del encargado que se va a eliminar.
     * @return true si el encargado se eliminó correctamente, false en caso
     *         contrario.
     * @throws UsernameNotFoundException si no se encuentra un usuario con el correo
     *                                   electrónico proporcionado.
     * @throws IllegalArgumentException  si el usuario con el correo electrónico
     *                                   proporcionado no es un encargado.
     */
    @Override
    public void deleteEncargado(String email) {

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) { throw new UsernameNotFoundException(email); }
        if (userEntity.getRol().getId() != 2) { throw new IllegalArgumentException("El usuario con el e-mail " + email + " no es encargado."); }

        try {
            userRepository.delete(userEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el usuario");
        }

    }

    /**
     * Genera una contraseña aleatoria de una longitud dada.
     * 
     * @param longitud de la nueva contraseña a generar.
     * @return String contraseña aleatoria generada.
     */
    public static String generarContrasena(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(rand.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    public String generarEnlaceConfirmacion(String token) {
        String tokenCodificado = UriUtils.encode(token, "UTF-8");
        return UriComponentsBuilder.fromHttpUrl(appBaseUrl.concat("/users/confirmar"))
                .pathSegment(tokenCodificado)
                .toUriString();
    }

}
