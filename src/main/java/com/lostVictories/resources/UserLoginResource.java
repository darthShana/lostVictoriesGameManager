package com.lostVictories.resources;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/userLogin")
public class UserLoginResource {

	private UserDAO userDAO;
	
	@Autowired
    public UserLoginResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
    
	@RequestMapping(path="", method=POST, consumes=APPLICATION_JSON_VALUE, produces=APPLICATION_JSON_VALUE)
	public User userLogin(@RequestBody User user, HttpServletRequest request) throws ServletException{
		
		User stored = userDAO.getUser(user.getUsername());
		request.login(user.getUsername(), user.getPassword1());
		return stored.clearPAsswords();
		
	}


	
	
}
