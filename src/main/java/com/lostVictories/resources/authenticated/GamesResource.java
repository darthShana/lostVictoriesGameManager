package com.lostVictories.resources.authenticated;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.Game;
import com.lostVictories.model.GameService;
import com.lostVictories.model.User;
import com.lostVictories.resources.exceptions.GameRequestFailureException;
import com.lostVictories.resources.exceptions.InvalidUserException;



@RestController
@RequestMapping("/authenticated/games")
public class GamesResource {

	private static Logger log = Logger.getLogger(GamesResource.class); 
	public static ObjectMapper MAPPER;
	static{
		MAPPER = new ObjectMapper();
	}
	

	private GameService gameService;
	private UserDAO userDAO;
    
	@Autowired
    public GamesResource(UserDAO userDAO, GameService gameService) {
		this.userDAO = userDAO;
		this.gameService = gameService;
	}
	
	@RequestMapping(path="", method=GET, produces=APPLICATION_JSON_VALUE)
    public List<Game> getGames(@RequestParam(value="userID", required=true) String userID) {
		UUID id;
		try{
			id = UUID.fromString(userID);
		}catch(Throwable e){
			throw new InvalidUserException("invalid user");
		}
		
		return gameService.getGameInfo(id);
	}
	
	@RequestMapping(path="", method=RequestMethod.POST, consumes=APPLICATION_JSON_VALUE, produces=APPLICATION_JSON_VALUE)
	public Game putGame(@RequestBody User _u) throws IOException{
		UUID id = _u.getId();
		log.info("put gam by user:"+id);
		User user = userDAO.getUser(id);
		try{
			return gameService.createGame(user);
		}catch(Exception e){
			throw new GameRequestFailureException(e.getMessage());
		}
	}
}

