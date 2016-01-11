package com.lostVictories.model;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;


import com.lostVictories.dao.GameDAO;

public class GameService {

	private GameDAO gameDAO;

	@Inject
	public GameService(GameDAO gameDAO) {
		this.gameDAO = gameDAO;
	}
	
	public List<Game> getGameInfo(UUID id) {
		return gameDAO.loadAllGames(id);
	}

	public void joinGame(String name, User user, String country) throws IOException {
		String indexName = name+"_unit_status";
		
		gameDAO.joinGame(indexName, user, country);
		
	}

}
