package com.lostVictories.model;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GameRequest {

    private UUID id;
	private String gameName;
	private long requestTime;
	private UUID requestUser;
	private String status;
	private String host;
	private Integer port;
	private String gameID;
	private Long startDate;
	private Long endDate;
	private String gameVersion;
	private String victor;

	private Map<String, String> players = new HashMap<>();
	private Map<String, String> playerCountries = new HashMap<>();


	public GameRequest(String gameName, User user) {
	    this.id = UUID.randomUUID();
		this.gameName = gameName;
		this.requestTime = System.currentTimeMillis();
		this.requestUser = user.getId();
		this.status = "REQUESTED";
	}

	public GameRequest(UUID id, Map<String, Object> source) {
	    this.id = id;
		this.gameName = (String) source.get("gameName");
		this.requestTime = (long) source.get("requestTime");
		this.requestUser = UUID.fromString((String) source.get("requestUser"));
		this.status = (String) source.get("status");
		this.host = (String) source.get("host");
		this.port = (Integer) source.get("port");
		this.gameID = (String) source.get("gameID");
		this.startDate = (Long) source.get("startDate");
		this.endDate = (Long) source.get("endDate");
		this.gameVersion = (String) source.get("gameVersion");
		this.victor = (String) source.get("victor");
		if(source.get("players")!=null) {
            this.players = (Map<String, String>) source.get("players");
        }
		if(source.get("playerCountries")!=null) {
            this.playerCountries = (Map<String, String>) source.get("playerCountries");
        }

	}

    public UUID getId() {
        return id;
    }

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public UUID getRequestUser() {
		return requestUser;
	}

	public void setRequestUser(UUID requestUser) {
		this.requestUser = requestUser;
	}

	public String getStatus() {
		return status;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getGameID() {
		return gameID;
	}

	public Long getStartDate() {
		return startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public String getVictor() {
		return victor;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public Map<String, String> getPlayers() {
		return players;
	}

	public Map<String, String> getPlayerCountries() {
		return playerCountries;
	}



	public void setStatus(String status) {
		this.status = status;
	}

	@JsonIgnore
	public XContentBuilder getJSONRepresentation() throws IOException {
		return jsonBuilder()
		            .startObject()
		                .field("gameName", gameName)
		                .field("requestTime", requestTime)
		                .field("requestUser", requestUser)
		                .field("status", status)
		            .endObject();
	}

	public void addPlayer(UUID user, UUID character, String country) {
		players.put(user.toString(), character.toString());
		playerCountries.put(user.toString(), country);
	}

}
