package com.lostVictories.model;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
public class User {

	private UUID id; 
	private String username;
	private String email;
	private String password1;
	private String password2;
	private String recaptchaResponse;
	
	public User() {}
	
	public User(UUID id, Map<String, Object> source) {
		this.id = id;
		this.username = (String)source.get("username");
		this.email = (String)source.get("email");
		this.password1 = (String)source.get("password1");
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword1() {
		return password1;
	}
	public void setPassword1(String password1) {
		this.password1 = password1;
	}
	public String getPassword2() {
		return password2;
	}
	public void setPassword2(String password2) {
		this.password2 = password2;
	}
	
	@JsonIgnore
	public XContentBuilder getJSONRepresentation() throws IOException {
		return jsonBuilder()
		            .startObject()
		                .field("username", username)
		                .field("email", email)
		                .field("password1", password1)
		            .endObject();
	}

	public UUID getId() {
		return id;
	}

	public User clearPAsswords() {
		password1 = null;
		password2 = null;
		return this;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getRecaptchaResponse() {
		return recaptchaResponse;
	}

	public void setRecaptchaResponse(String recaptchaResponse) {
		this.recaptchaResponse = recaptchaResponse;
	}
	
}
