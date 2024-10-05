package com.admision.maestrias.api.pam.service.implementations;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import com.admision.maestrias.api.pam.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.admision.maestrias.api.pam.security.SecurityConstants;
import com.admision.maestrias.api.pam.service.interfaces.JWTServiceInterface;
import com.admision.maestrias.api.pam.shared.SimpleGrantedAuthorityMixin;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.admision.maestrias.api.pam.repository.UserRepository;

/**
 * @author Juan Pablo Correo Tarazona, Angel García
 */
@Component
public class JWTService implements JWTServiceInterface {

	@Autowired
	private UserRepository userR;

	@Override
	public String create(Authentication auth) throws IOException {

		String username = ((User) auth.getPrincipal()).getUsername(); //aca se pone el correo 

		int idUser = userR.findByEmail(username).getId();

		Collection<? extends GrantedAuthority> roles = auth.getAuthorities();

		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
		claims.put("idUser", idUser);
		String token = Jwts.builder().setClaims(claims).setSubject(username)
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret().getBytes()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE)).compact();

		return token;
	}

	@Override
	public boolean validate(String token) {
		try {
			getClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}

	}

	@Override
	public Claims getClaims(String token) {
		Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret().getBytes())
				.parseClaimsJws(resolve(token)).getBody();
		return claims;
	}

	public Claims getClaims2(String token) {
		Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret().getBytes())
				.parseClaimsJws(token).getBody();
		return claims;
	}

	public Integer getId2(String token) {
		return (Integer) getClaims2(token).get("idUser");
	}

	@Override
	public String getUsername(String token) {
		return getClaims(token).getSubject();
	}

	public Integer getId(String token) {
		return (Integer) getClaims(token).get("idUser");
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
		Object roles = getClaims(token).get("authorities");

		Collection<? extends GrantedAuthority> authorities = Arrays
				.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
						.readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));

		return authorities;
	}

	@Override
	public String resolve(String token) {
		if (token != null && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			return token.replace(SecurityConstants.TOKEN_PREFIX, "");
		}
		return null;
	}

	public String generarToken(UserEntity usuario) {

		Claims claims = Jwts.claims();
		claims.put("idUser", usuario.getId());
		String token = Jwts.builder().setClaims(claims).setSubject(usuario.getEmail())
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret().getBytes()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE)).compact();

		return token;
	}

}
