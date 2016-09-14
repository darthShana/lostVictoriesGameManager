package com.lostVictories.resources;


import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;
import com.lostVictories.resources.exceptions.InvalidRequestException;

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
	public User userLogin(@RequestBody User user) throws IOException{
		
		User stored = userDAO.getUser(user.getUsername());
		if(stored!=null && BCrypt.checkpw(user.getPassword1(), stored.getPassword1())){
			stored.clearPAsswords();
			return stored;
		}
		
		throw new InvalidRequestException("Invalid user name or password");
		
	}


	
	
}
