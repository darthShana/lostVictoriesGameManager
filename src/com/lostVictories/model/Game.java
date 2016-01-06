package com.lostVictories.model;

import java.util.UUID;

public class Game {

	private UUID id;

	public Game(UUID id) {
		this.setId(id);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

}

