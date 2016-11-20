package com.lostVictories.resources.authenticated;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;
import com.lostVictories.resources.exceptions.UserNotFoundException;

@RestController
@RequestMapping("/authenticated/user")
public class UserResource {

	private UserDAO userDAO;
	
	@Autowired
    public UserResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@RequestMapping(path="", method=GET, produces=APPLICATION_JSON_VALUE)
	public User getLogedInUser(HttpServletRequest request) throws ServletException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth!=null && !"anonymousUser".equals(auth.getPrincipal())){
			return userDAO.getUser(((User)auth.getPrincipal()).getId()).clearPAsswords();
		}
		throw new UserNotFoundException();
	}
}