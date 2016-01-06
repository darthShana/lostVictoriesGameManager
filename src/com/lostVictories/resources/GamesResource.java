package com.lostVictories.resources;

import java.util.UUID;

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
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
    public Response getGames() {
		ArrayNode list = MAPPER.createArrayNode();
		list.add(MAPPER.valueToTree(new Game(UUID.randomUUID())));
		list.add(MAPPER.valueToTree(new Game(UUID.randomUUID())));
		return Response.ok().header("Access-Control-Allow-Origin", "*").entity(list).build();
	}
}

