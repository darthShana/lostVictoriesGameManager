package com.lostVictories.resources;
import static com.lostVictories.resources.UserLoginResource.returnSuccess;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.lostVictories.model.Game;
import com.lostVictories.model.GameService;


@Path("/games")
public class GamesResource {

	public static ObjectMapper MAPPER;
	static{
		MAPPER = new ObjectMapper();
	}
	
	@Context
    UriInfo uriInfo;
    @Context
    Request request;
	private GameService gameService;
    
	@Inject
    public GamesResource(GameService gameService) {
		this.gameService = gameService;
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
    public Response getGames() {
		String u = uriInfo.getQueryParameters().getFirst("userID");
		UUID id = UUID.fromString(u);
		
		ArrayNode list = MAPPER.createArrayNode();
		gameService.getGameInfo(id).stream().forEach(g->list.add(MAPPER.valueToTree(g)));
		return returnSuccess(list);
	}
}

