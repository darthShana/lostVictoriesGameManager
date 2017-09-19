package com.lostVictories.dao;

import static com.lostVictories.dao.UserDAO.getESClient;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Repository;

import com.lostVictories.model.GameRequest;
import com.lostVictories.model.User;

@Repository
public class GameRequestDAO {
	
	private Client esClient;
	private String indexName = "game_request";
	private static Logger log = Logger.getLogger(GameRequestDAO.class); 

	public GameRequestDAO() throws IOException {
		esClient = getESClient();
		IndicesAdminClient adminClient = esClient.admin().indices();
		final IndicesExistsResponse res = adminClient.prepareExists(indexName).execute().actionGet();
		if (!res.isExists()) {
			final CreateIndexRequestBuilder createIndexRequestBuilder = adminClient.prepareCreate(indexName);
			
		    XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("properties");
		    builder.startObject("gameName")
	        	.field("type", "string")
	        	.field("index", "not_analyzed")
	        	.field("store", "yes")
	        	.endObject();
		    builder.startObject("requestTime")
	        	.field("type", "long")
	        	.field("index", "not_analyzed")
	        	.field("store", "yes")
	        	.endObject();
		    builder.startObject("requestUser")
	        	.field("type", "string")
	        	.field("index", "not_analyzed")
	        	.field("store", "yes")
	        	.endObject();
		    builder.startObject("status")
	        	.field("type", "string")
	        	.field("index", "not_analyzed")
	        	.field("store", "yes")
	        	.endObject();
			    
		    createIndexRequestBuilder.addMapping(indexName, builder);
		    createIndexRequestBuilder.execute().actionGet();
        }
	}

	public String findUnusedGameName(String[] availableBattles) {
		for(String name: availableBattles){
			if(isUnused(name)){
				return name;
			}

		}
		return null;
	}

	private boolean isUnused(String name) {
		IndicesAdminClient adminClient = esClient.admin().indices();
		final IndicesExistsResponse res = adminClient.prepareExists(indexName).execute().actionGet();
		if (!res.isExists()) {
			return true;
		}
		
		FilteredQueryBuilder builder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				   FilterBuilders.termFilter("gameName",name));
		SearchResponse response =
				esClient.prepareSearch(indexName).setTypes(indexName)
			       .setQuery(builder)
			       .execute()
			       .actionGet();
		if(response.getHits().getTotalHits() == 0){
			return true;
		}
		return false;
	}

    public GameRequest getByName(String name) {
        FilteredQueryBuilder builder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
                FilterBuilders.termFilter("gameName",name));
        SearchResponse response =
                esClient.prepareSearch(indexName).setTypes(indexName)
                        .setQuery(builder)
                        .execute()
                        .actionGet();
        if(response.getHits().getTotalHits() == 0){
            return null;
        }
        SearchHit hit = response.getHits().iterator().next();
        return new GameRequest(UUID.fromString(hit.getId()), hit.getSource());

    }

	public void cretaeGameRequest(String gameName, User user) throws IOException {
		GameRequest gameRequest = new GameRequest(gameName, user);
		
		try {
			esClient.prepareIndex(indexName, indexName, UUID.randomUUID().toString())
			        .setSource(gameRequest.getJSONRepresentation())
			        .execute()
			        .actionGet();

			esClient.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
            DockerClient dockerClient = DockerClientBuilder.getInstance().build();

			Volume volume1 = new Volume("/etc/config/lostVictoriesServer.properties");
            ExposedPort tcp5055 = ExposedPort.tcp(5055);
            Ports portBindings = new Ports();
            portBindings.bind(tcp5055, Ports.Binding.bindPort(5055));

			CreateContainerResponse container = dockerClient.createContainerCmd("darthshana/lostvictoryserver:0.0.1-SNAPSHOT")
					.withVolumes(volume1)
					.withBinds(new Bind("/home/darthshana/gameEngine/lostVictoriesServer.properties", volume1))
//					.withBinds(new Bind("/Users/dharshanar/development/eclipse/lostVictoriesSever/src/test/resources/server.properties", volume1))
                    .withExposedPorts(tcp5055)
                    .withPortBindings(portBindings)
                    .withLinks(new Link("redis", "redis"), new Link("elasticsearch", "elasticsearch"))
                    .withEnv("GAME_NAME="+gameName, "GAME_PORT=5055")
//					.withCmd("/usr/bin/java", "-jar", "/usr/lostvictories/app.jar", "-P/etc/config/lostVictoriesServer.properties", gameName, "5055")
					.exec();

			dockerClient.startContainerCmd(container.getId()).exec();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	public Set<GameRequest> getAll() {
		SearchResponse searchResponse = esClient.prepareSearch(indexName)
                .setQuery(matchAllQuery()).setSize(10000)
                .execute().actionGet();
		
		log.trace("retrieved :"+searchResponse.getHits().hits().length+" requests from elasticsearch");
		Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
		Iterable<SearchHit> iterable = () -> iterator;
		return StreamSupport.stream(iterable.spliterator(), true).map(hit -> new GameRequest(UUID.fromString(hit.getId()), hit.getSource())).collect(Collectors.toSet());
		
	}

	public void updatePlayers(GameRequest byName) throws IOException {
		esClient.prepareUpdate(indexName, indexName, byName.getId().toString())
				.setDoc(jsonBuilder()
						.startObject()
						.field("players", byName.getPlayers())
						.field("playerCountries", byName.getPlayerCountries())
						.endObject()
				)
				.get();
	}

//	public static void main(String[] args) throws IOException {
//        new GameRequestDAO().cretaeGameRequest("test", new User());
//        System.out.println("started");
//    }
}
