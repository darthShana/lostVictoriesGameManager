package com.lostVictories.dao;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.lostVictories.model.User;

@Singleton
public class UserDAO {

	private Client esClient;
	private String indexName = "users";
	
	@Inject
	public UserDAO() throws IOException {
		esClient = getESClient();
		IndicesAdminClient adminClient = esClient.admin().indices();
		final IndicesExistsResponse res = adminClient.prepareExists(indexName).execute().actionGet();
		if (!res.isExists()) {
			final CreateIndexRequestBuilder createIndexRequestBuilder = adminClient.prepareCreate(indexName);
			
		    XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("user").startObject("properties");
		    builder.startObject("username")
	        	.field("type", "string")
	        	.field("store", "yes")
	        	.endObject();
		    builder.startObject("email")
	        	.field("type", "string")
	        	.field("store", "yes")
	        	.endObject();
		    builder.startObject("password1")
	        	.field("type", "string")
	        	.field("store", "yes")
	        	.endObject();
			    
		    createIndexRequestBuilder.addMapping("user", builder);
		    createIndexRequestBuilder.execute().actionGet();
        }
	}
	
	public boolean existsUsername(String username) {
		SearchResponse response =
				esClient.prepareSearch(indexName).setTypes("user")
			          .setQuery(
			                   matchQuery("username", username))
			          .execute()
			          .actionGet();
		return response.getHits().getTotalHits()!=0;
	}

	public boolean existsEmail(String email) {
		SearchResponse response =
				esClient.prepareSearch(indexName).setTypes("user")
			          .setQuery(
			        		  matchQuery("email", email))
			          .execute()
			          .actionGet();
		return response.getHits().getTotalHits()!=0;
	}
	
	private Client getESClient() {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
		TransportClient transportClient = new TransportClient(settings);
		transportClient = transportClient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		return (Client) transportClient;

		
	}

	public void createUser(User user) {
		
		try {
			esClient.prepareIndex(indexName, "unitStatus", user.getId().toString())
			        .setSource(user.getJSONRepresentation())
			        .execute()
			        .actionGet();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

}
