package com.lostVictories.model;

import java.util.Map;
import java.util.UUID;

public class Game {

	private UUID id;
	private String name;
	private String host;
	private String port;
	private long startDate;
	private boolean joined;
	private UUID avatarID;
	private String gameVersion;

	public Game(){}

	public Game(Map<String, Object> source) {
		id = UUID.fromString((String)source.get("gameID"));
		name = (String) source.get("name");
		host = (String) source.get("host");
		port = source.get("port")+"";
		startDate =  (long) source.get("startDate");
		gameVersion = (String) source.get("gameVersion");
	}

	public Game(Map<String, Object> source, String id) {
		this(source);
		avatarID = UUID.fromString(id);
		joined = true;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public boolean isJoined() {
		return joined;
	}

	public void setJoined(boolean joined) {
		this.joined = joined;
	}

	public UUID getAvatarID() {
		return avatarID;
	}

	public void setAvatarID(UUID avatarID) {
		this.avatarID = avatarID;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

}

