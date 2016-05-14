package com.lostVictories.model;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;


import com.lostVictories.dao.GameDAO;
import com.lostVictories.dao.GameRequestDAO;

public class GameService {

	private static final int MAX_ALLOWED_RUNNING = 1;
	private GameDAO gameDAO;
	private GameRequestDAO gameRequestDAO;

	@Inject
	public GameService(GameDAO gameDAO, GameRequestDAO gameRequestDAO) {
		this.gameDAO = gameDAO;
		this.gameRequestDAO = gameRequestDAO;
	}
	
	public List<Game> getGameInfo(UUID id) {
		return gameDAO.loadAllGames(id);
	}

	public void joinGame(String name, User user, String country) throws IOException {
		String indexName = name+"_unit_status";
		
		gameDAO.joinGame(indexName, user, country);
		
	}
	
	public Game createGame(User user) throws IOException {
		String gameName = gameRequestDAO.findUnusedGameName(AvailableBattles.availableBattles);
		if(gameName == null){
			throw new RuntimeException("no game name unavailable");
		}
		List<Game> existingGames = gameDAO.loadAllGames(user.getId());
		existingGames.stream().filter(g -> "inProgress".equals(g.getGameStatus()));
		
		Set<GameRequest> existingRequests = gameRequestDAO.getAll();
		existingGames.stream().filter(g -> "REQUESTED".equals(g.getGameStatus()));
		
		if(existingGames.size()+existingRequests.size()>=MAX_ALLOWED_RUNNING){
			throw new RuntimeException("game limit exceeded");
		}
		
		gameRequestDAO.cretaeGameRequest(gameName, user);
		
		Game game = new Game();
		game.setName(gameName);
		return game;
	}

}
