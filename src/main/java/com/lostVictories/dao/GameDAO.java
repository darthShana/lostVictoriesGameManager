package com.lostVictories.dao;

import static com.lostVictories.dao.UserDAO.getESClient;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;

import java.io.IOException;
import java.util.UUID;


import com.lostVictories.api.JoinRequest;
import com.lostVictories.api.LostVictoriesServerGrpc;
import com.lostVictories.model.GameRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Repository;

import com.lostVictories.model.User;

@Repository
public class GameDAO {

	private Client esClient;

	public GameDAO() {
		esClient = getESClient();
	}

	public UUID joinGame(GameRequest game, User user, String country) throws IOException {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", game.getPort())
				.usePlaintext(true)
				.build();
		LostVictoriesServerGrpc.LostVictoriesServerBlockingStub stub = LostVictoriesServerGrpc.newBlockingStub(managedChannel);
		JoinRequest joinResponse = stub.joinGame(JoinRequest.newBuilder()
				.setUserID(user.getId().toString())
				.setCountry(country)
				.build());
		return UUID.fromString(joinResponse.getCharacterID());
	}
	
	private String createBootCampObjective(String country)  {
		
		if("GERMAN".equals(country)){
			return "{\"classType\":\"com.jme3.lostVictories.objectives.CompleteBootCamp\",\"location\":{\"x\":246.29144287109375,\"y\":96.77545928955078,\"z\":55.41226577758789}}";
		}else{
			return "{\"classType\":\"com.jme3.lostVictories.objectives.CompleteBootCamp\",\"location\":{\"x\":-57.21826,\"y\":96.380104,\"z\":-203.38945}}";
		}
		
	}

}
