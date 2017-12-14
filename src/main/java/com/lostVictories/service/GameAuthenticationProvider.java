package com.lostVictories.service;

import java.util.HashSet;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;

@Service
public class GameAuthenticationProvider implements AuthenticationProvider {

	private static Logger log = LoggerFactory.getLogger(GameAuthenticationProvider.class);
	private UserDAO userDAO;

	public GameAuthenticationProvider(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	public Authentication authenticate(Authentication arg0) throws AuthenticationException {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) arg0;
		String username = String.valueOf(auth.getPrincipal());
		String password = String.valueOf(auth.getCredentials());
		log.info("attempting login for user:"+username);
		User stored = userDAO.getUser(username);
		if(stored!=null && BCrypt.checkpw(password, stored.getPassword1())){
			stored.clearPAsswords();
			Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
	        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			return new UsernamePasswordAuthenticationToken(stored, null, authorities) ;
		}
		throw new BadCredentialsException("Invalid username or password");
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}
