package com.lostVictories.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lostVictories.dao.GameDAO;
import com.lostVictories.dao.GameRequestDAO;

@Service
public class GameService {

	private static Logger log = Logger.getLogger(GameService.class); 
	private static final int MAX_ALLOWED_RUNNING = 1;
	private GameDAO gameDAO;
	private GameRequestDAO gameRequestDAO;

	@Autowired
	public GameService(GameDAO gameDAO, GameRequestDAO gameRequestDAO) {
		this.gameDAO = gameDAO;
		this.gameRequestDAO = gameRequestDAO;
	}
	
	public List<Game> getGameInfo(UUID id) {
		List<Game> ret = new ArrayList<>();
		gameRequestDAO.getAll().forEach(g->{
			if(g.getPlayers().containsKey(id)){
				ret.add(new Game(g, g.getPlayers().get(id), g.getPlayerCountries().get(id)));
			}else{
				ret.add(new Game(g));
			}
		});
		return ret;
	}

	public void joinGame(String instance, User user, String country) throws IOException {
		String indexName = instance+"_unit_status";
		
		gameDAO.joinGame(indexName, user, country);
		
	}
	
	public Game createGame(User user) throws IOException {
		String gameName = gameRequestDAO.findUnusedGameName(AvailableBattles.availableBattles);
		if(gameName == null){
			throw new RuntimeException("no game name unavailable");
		}
		List<Game> existingGames = getGameInfo(user.getId());
		existingGames = existingGames.stream().filter(g -> "inProgress".equals(g.getGameStatus())).collect(Collectors.toList());
		log.info("existing inprogress games:"+existingGames);
		
		Set<GameRequest> existingRequests = gameRequestDAO.getAll();
		existingRequests = existingRequests.stream().filter(g -> "REQUESTED".equals(g.getStatus())).collect(Collectors.toSet());
		log.info("existing inprogress requestes:"+existingRequests);
		
		if(existingGames.size()+existingRequests.size()>=MAX_ALLOWED_RUNNING){
			throw new RuntimeException("game limit exceeded");
		}
		
		gameRequestDAO.cretaeGameRequest(gameName, user);
		
		Game game = new Game(gameName);
		return game;
	}

}
