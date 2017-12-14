package com.lostVictories.resources;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/userLogin")
public class UserLoginResource {

	private static Logger log = LoggerFactory.getLogger(UserLoginResource.class);
	private UserDAO userDAO;
	
	@Autowired
    public UserLoginResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
    
	@RequestMapping(path="", method=POST, consumes=APPLICATION_JSON_VALUE, produces=APPLICATION_JSON_VALUE)
	public User userLogin(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		User stored = userDAO.getUser(user.getUsername());
		try {
			request.login(user.getUsername(), user.getPassword1());
			return stored.clearPAsswords();
		} catch (ServletException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorised");
			return null;
		}
		
	}


	
	
}
