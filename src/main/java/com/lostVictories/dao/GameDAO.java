package com.lostVictories.dao;

import static com.lostVictories.dao.UserDAO.getESClient;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.FilterBuilders.andFilter;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


import com.lostVictories.model.GameRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Repository;

import com.lostVictories.model.Game;
import com.lostVictories.model.User;

@Repository
public class GameDAO {

	private Client esClient;

	public GameDAO() {
		esClient = getESClient();
	}

	public void joinGame(String indexName, User user, String country) throws IOException {
		FilteredQueryBuilder builder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
					andFilter(termFilter("type","SOLDIER"), termFilter("rank","CADET_CORPORAL"), termFilter("country",country)));
		
		
		SearchResponse response =
				esClient.prepareSearch(indexName).setTypes("unitStatus")
			       .setQuery(builder)
			       .execute()
			       .actionGet();
		
		Iterator<SearchHit> iterator = response.getHits().iterator();
		Iterable<SearchHit> iterable = () -> iterator;
		List<String> available = StreamSupport.stream(iterable.spliterator(), true).map(hit -> hit.getId()).collect(Collectors.toList());
		
		if(available.isEmpty()){
			throw new RuntimeException("Unable to find");
		}
		
		String selected = available.get(new Random().nextInt(available.size()));
//		HashMap<String, String> objectives = new HashMap<String, String>();
//		objectives.put(UUID.randomUUID().toString(), createBootCampObjective(country));
		XContentBuilder update = jsonBuilder()
	            .startObject()
                .field("type", "AVATAR")
                .field("userID", user.getId())
//                .field("objectives", MAPPER.writeValueAsString(objectives))
            .endObject();
		System.out.println("converting to avatar:"+selected);
		esClient.prepareUpdate(indexName, "unitStatus", selected).setDoc(update).execute().actionGet();
		esClient.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
		
	}
	
	private String createBootCampObjective(String country)  {
		
		if("GERMAN".equals(country)){
			return "{\"classType\":\"com.jme3.lostVictories.objectives.CompleteBootCamp\",\"location\":{\"x\":246.29144287109375,\"y\":96.77545928955078,\"z\":55.41226577758789}}";
		}else{
			return "{\"classType\":\"com.jme3.lostVictories.objectives.CompleteBootCamp\",\"location\":{\"x\":-57.21826,\"y\":96.380104,\"z\":-203.38945}}";
		}
		
	}

}
