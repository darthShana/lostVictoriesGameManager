package com.lostVictories.dao;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;


import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Repository;

import com.lostVictories.model.User;

@Repository
public class UserDAO {

	private Client esClient;
	private String indexName = "users";
	private static TransportClient transportClient;
	
	public UserDAO() throws IOException {
		esClient = getESClient();
		IndicesAdminClient adminClient = esClient.admin().indices();
		final IndicesExistsResponse res = adminClient.prepareExists(indexName).execute().actionGet();
		if (!res.isExists()) {
			final CreateIndexRequestBuilder createIndexRequestBuilder = adminClient.prepareCreate(indexName);
			
		    XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("user").startObject("properties");
		    builder.startObject("username")
	        	.field("type", "string")
	        	.field("index", "not_analyzed")
	        	.field("store", "yes")
	        	.endObject();
		    builder.startObject("email")
	        	.field("type", "string")
	        	.field("index", "not_analyzed")
	        	.field("store", "yes")
	        	.endObject();
		    builder.startObject("password1")
	        	.field("type", "string")
	        	.field("index", "not_analyzed")
	        	.field("store", "yes")
	        	.endObject();
			    
		    createIndexRequestBuilder.addMapping("user", builder);
		    createIndexRequestBuilder.execute().actionGet();
        }
	}
	
	public boolean existsUsername(String username) {
		FilteredQueryBuilder builder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				   FilterBuilders.termFilter("username",username));
		SearchResponse response =
				esClient.prepareSearch(indexName).setTypes("user")
			       .setQuery(builder)
			       .execute()
			       .actionGet();
		return response.getHits().getTotalHits()!=0;
	}

	public boolean existsEmail(String email) {
		FilteredQueryBuilder builder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				   FilterBuilders.termFilter("email",email));
		SearchResponse response =
				esClient.prepareSearch(indexName).setTypes("user")
			       .setQuery(builder)
			       .execute()
			       .actionGet();
		return response.getHits().getTotalHits()!=0;
	}
	
	public User getUser(String username) {
		FilteredQueryBuilder builder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				   FilterBuilders.termFilter("username",username));
		SearchResponse response =
				esClient.prepareSearch(indexName).setTypes("user")
			       .setQuery(builder)
			       .execute()
			       .actionGet();
		Iterator<SearchHit> iterator = response.getHits().iterator();
		if(!iterator.hasNext()){
			return null;
		}
		SearchHit next = iterator.next();
		return new User(UUID.fromString(next.getId()), next.getSource());
	}
	
	public static Client getESClient() {
		if(transportClient==null){
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "lostVictories").build();
			transportClient = new TransportClient(settings);
			transportClient = transportClient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		}
		return (Client) transportClient;

		
	}

	public void createUser(User user, UUID id) {
		
		try {
			esClient.prepareIndex(indexName, "user", id.toString())
			        .setSource(user.getJSONRepresentation())
			        .execute()
			        .actionGet();
			esClient.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	public User getUser(UUID id) {
		GetResponse response = esClient.prepareGet(indexName, "user", id.toString())
		        .execute()
		        .actionGet();
		return new User(UUID.fromString(response.getId()), response.getSource());
	}

}
