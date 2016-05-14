package com.lostVictories.resources;

import java.io.IOException;
import java.util.UUID;

import static com.lostVictories.resources.GamesResource.MAPPER;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.node.ArrayNode;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.Game;
import com.lostVictories.model.GameService;

@Path("/joinGame")
public class JoinGameResource {

	@Context
    UriInfo uriInfo;
    @Context
    Request request;
	private GameService gameService;
	private UserDAO userDAO;
    
    @Inject
    public JoinGameResource(UserDAO userDAO, GameService gameService) {
		this.userDAO = userDAO;
		this.gameService = gameService;
	}
    
    @GET
	@Produces({MediaType.APPLICATION_JSON})
    public Response joinGame() throws JsonProcessingException, IOException{
    	String u = uriInfo.getQueryParameters().getFirst("userID");
    	String country = uriInfo.getQueryParameters().getFirst("country");
		UUID id = UUID.fromString(u);
		
		Game game = MAPPER.readValue(uriInfo.getQueryParameters().getFirst("game"), Game.class);
    	
		gameService.joinGame(game.getName(), userDAO.getUser(id), country);
		
    	ArrayNode list = MAPPER.createArrayNode();
		gameService.getGameInfo(id).stream().forEach(g->list.add(MAPPER.valueToTree(g)));
		return Response.ok().entity(list).build();
    }
}
