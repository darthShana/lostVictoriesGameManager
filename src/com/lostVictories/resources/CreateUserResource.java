package com.lostVictories.resources;

import static com.lostVictories.resources.GamesResource.MAPPER;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.mindrot.jbcrypt.BCrypt;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;

@Path("/createUser")
public class CreateUserResource {

	@Context
    UriInfo uriInfo;
    @Context
    Request request;
	private UserDAO userDAO;
	
	@Inject
    public CreateUserResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
    
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response createUser() throws JsonProcessingException, IOException{
		
		JsonNode u = MAPPER.readTree(uriInfo.getQueryParameters().getFirst("user"));
		User user = MAPPER.treeToValue(u, User.class);
		if(userDAO.existsUsername(user.getUsername())){
			return returnError(Status.BAD_REQUEST, "Username already exists");
		}
		if(userDAO.existsEmail(user.getEmail())){
			return returnError(Status.BAD_REQUEST, "email already registered");
		}
		if(user.getPassword1()==null || user.getPassword1().isEmpty() || !user.getPassword1().equals(user.getPassword2())){
			return returnError(Status.BAD_REQUEST, "password invalid");
		}else{
			user.setPassword1(BCrypt.hashpw(user.getPassword1(), BCrypt.gensalt()));
		}
		
		userDAO.createUser(user);
		
		return returnSuccess(user);
	}

	private Response returnSuccess(User user) {
		return Response.ok().header("Access-Control-Allow-Origin", getCrossDomainString()).entity(user).build();
	}

	private Response returnError(Status status, String message) {
		return Response.status(status).header("Access-Control-Allow-Origin", getCrossDomainString()).entity(message).build();
	}

	private String getCrossDomainString() {
		return "*";
	}
	
	
}
