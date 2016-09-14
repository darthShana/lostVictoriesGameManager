package com.lostVictories.resources.authenticated;

import static com.lostVictories.resources.authenticated.GamesResource.MAPPER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.Game;
import com.lostVictories.model.GameService;

@RestController
@RequestMapping("/authenticated/joinGame")
public class JoinGameResource {

	private GameService gameService;
	private UserDAO userDAO;
    
    @Autowired
    public JoinGameResource(UserDAO userDAO, GameService gameService) {
		this.userDAO = userDAO;
		this.gameService = gameService;
	}
    
    @RequestMapping(path="", method=POST, consumes=APPLICATION_JSON_VALUE, produces=APPLICATION_JSON_VALUE)
    public List<Game> joinGame(@RequestBody Map<String, Object> payload) throws IOException{
    	String u = (String) payload.get("userID");
    	String country = (String) payload.get("country");
		UUID id = UUID.fromString(u);
		
		Game game = MAPPER.readValue((String)payload.get("game"), Game.class);
		gameService.joinGame(game.getId(), userDAO.getUser(id), country);
		return gameService.getGameInfo(id);
    }
}
