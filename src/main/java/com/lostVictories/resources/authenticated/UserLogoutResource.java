package com.lostVictories.resources.authenticated;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticated/userLogout")
public class UserLogoutResource {

	
    @RequestMapping(path="", method=POST, consumes=APPLICATION_JSON_VALUE)
	public void userLogout(@RequestBody Map<String, Object> payload, HttpServletRequest request) throws ServletException{
		request.logout();
	}
}
