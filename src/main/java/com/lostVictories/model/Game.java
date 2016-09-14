package com.lostVictories.model;

import java.util.Map;
import java.util.UUID;

public class Game {

	private String id;
	private String name;
	private String host;
	private String port;
	private long startDate;
	private boolean joined;
	private UUID avatarID;
	private String gameVersion;
	private String gameStatus;
	private String victor;
	private Long endDate;
	private String country;

	public Game(){}

	public Game(Map<String, Object> source) {
		id = (String)source.get("gameID");
		name = (String) source.get("name");
		host = (String) source.get("host");
		port = source.get("port")+"";
		startDate =  (long) source.get("startDate");
		gameVersion = (String) source.get("gameVersion");
		setGameStatus((String) source.get("gameStatus"));
		setVictor((String) source.get("victor"));
		setEndDate((Long) source.get("endDate"));
	}

	public Game(Map<String, Object> source, String id, String country) {
		this(source);
		avatarID = UUID.fromString(id);
		joined = true;
		this.setCountry(country);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}

	public String getVictor() {
		return victor;
	}

	public void setVictor(String victor) {
		this.victor = victor;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}

