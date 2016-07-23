package com.lostVictories.resources;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.Game;
import com.lostVictories.model.GameService;
import com.lostVictories.model.User;

import javax.ws.rs.core.Response.Status;


@Path("/games")
public class GamesResource {

	private static Logger log = Logger.getLogger(GamesResource.class); 
	public static ObjectMapper MAPPER;
	static{
		MAPPER = new ObjectMapper();
	}
	
	@Context
    UriInfo uriInfo;
    @Context
    Request request;
	private GameService gameService;
	private UserDAO userDAO;
    
	@Inject
    public GamesResource(UserDAO userDAO, GameService gameService) {
		this.userDAO = userDAO;
		this.gameService = gameService;
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
    public Response getGames() {
		String u = uriInfo.getQueryParameters().getFirst("userID");
		UUID id;
		try{
			id = UUID.fromString(u);
		}catch(Exception e){
			return Response.status(Status.UNAUTHORIZED).entity("invalid user").build();
		}
		
		ArrayNode list = MAPPER.createArrayNode();
		gameService.getGameInfo(id).stream().forEach(g->list.add(MAPPER.valueToTree(g)));
		return Response.ok().entity(list).build();
	}
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response putGame(JsonNode _u) throws IOException{
		String asText = _u.get("id").asText();
		log.info("put gam by user:"+asText);
		User user = userDAO.getUser(UUID.fromString(asText));
		try{
			Game createGame = gameService.createGame(user);
			return Response.ok().entity(createGame).build();
		}catch(Exception e){
			return Response.status(Status.PRECONDITION_FAILED).entity(e.getMessage()).build();
		}
	}
}

