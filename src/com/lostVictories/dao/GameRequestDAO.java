package com.lostVictories.dao;

import static com.lostVictories.dao.UserDAO.getESClient;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

import com.google.inject.Singleton;
import com.lostVictories.model.GameRequest;
import com.lostVictories.model.User;

@Singleton
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
			
		    XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(indexName).startObject("properties");
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

	public void cretaeGameRequest(String gameName, User user) throws IOException {
		GameRequest gameRequest = new GameRequest(gameName, user);
		
		try {
			esClient.prepareIndex(indexName, indexName, UUID.randomUUID().toString())
			        .setSource(gameRequest.getJSONRepresentation())
			        .execute()
			        .actionGet();
			
			esClient.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	public Set<GameRequest> getAll() {
		SearchResponse searchResponse = esClient.prepareSearch(indexName)
                .setQuery(matchAllQuery()).setSize(10000)
                .execute().actionGet();
		
		log.trace("retrived :"+searchResponse.getHits().hits().length+" houses from elasticsearch");
		Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
		Iterable<SearchHit> iterable = () -> iterator;
		return StreamSupport.stream(iterable.spliterator(), true).map(hit -> new GameRequest(UUID.fromString(hit.getId()), hit.getSource())).collect(Collectors.toSet());
		
	}

}
