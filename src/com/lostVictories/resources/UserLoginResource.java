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

@Path("/userLogin")
public class UserLoginResource {

	@Context
    UriInfo uriInfo;
    @Context
    Request request;
	private UserDAO userDAO;
	
	@Inject
    public UserLoginResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
    
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response userLogin() throws JsonProcessingException, IOException{
		JsonNode u = MAPPER.readTree(uriInfo.getQueryParameters().getFirst("user"));
		User user = MAPPER.treeToValue(u, User.class);
		
		User stored = userDAO.getUser(user.getUsername());
		if(stored!=null && BCrypt.checkpw(user.getPassword1(), stored.getPassword1())){
			stored.clearPAsswords();
			return returnSuccess(stored);
		}
		return returnError(Status.BAD_REQUEST, "Invalid user name or password");
		
	}

	public static Response returnSuccess(Object user) {
		return Response.ok().header("Access-Control-Allow-Origin", getCrossDomainString()).entity(user).build();
	}

	public static Response returnError(Status status, String message) {
		return Response.status(status).header("Access-Control-Allow-Origin", getCrossDomainString()).entity(message).build();
	}

	private static String getCrossDomainString() {
		return "*";
	}
	
	
}
