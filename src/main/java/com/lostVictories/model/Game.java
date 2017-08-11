package com.lostVictories.model;

import com.lostVictories.api.Country;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class Game implements Serializable{

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
	private Country country;

	private Game(){}

	public Game(GameRequest source) {
		id = source.getGameID();
		name = source.getGameName();
		host = source.getHost();
		port = source.getPort().toString();
		startDate =  source.getStartDate();
		endDate = source.getEndDate();
		gameVersion = source.getGameVersion();
		gameStatus = source.getStatus();
		victor = source.getVictor();
	}

	public Game(GameRequest gameRequest, String id, Country country) {
		this(gameRequest);
		avatarID = UUID.fromString(id);
		joined = true;
		this.country = country;
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

	public Country getCountry() {
		return country;
	}

}

