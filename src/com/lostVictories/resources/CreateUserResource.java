package com.lostVictories.resources;

import static com.lostVictories.resources.GamesResource.MAPPER;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.mindrot.jbcrypt.BCrypt;

import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;

@Path("/createUser")
public class CreateUserResource {

	public static final String url = "https://www.google.com/recaptcha/api/siteverify";
    public static final String secret = "6LeQ0xUTAAAAAIfp98rXXBJMa_43xUX7EMO2YNST";
    private final static String USER_AGENT = "Mozilla/5.0";
	
	@Context
    UriInfo uriInfo;
    @Context
    Request request;
	private UserDAO userDAO;
	
	@Inject
    public CreateUserResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
    
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response createUser() throws JsonProcessingException, IOException{
		
		JsonNode u = MAPPER.readTree(uriInfo.getQueryParameters().getFirst("user"));
		User user = MAPPER.treeToValue(u, User.class);
		
		boolean recapchaResponse = verifyRecapchaResponse(user.getRecaptchaResponse());
		
		if(!recapchaResponse){
			return Response.status(Status.BAD_REQUEST).entity("Capcha failed!").build();			
		}
		if(null==user.getUsername() || userDAO.existsUsername(user.getUsername())){
			return Response.status(Status.BAD_REQUEST).entity("Username already exists").build();			
		}
		if(userDAO.existsEmail(user.getEmail())){
			return Response.status(Status.BAD_REQUEST).entity("email already registered").build();
		}
		if(user.getPassword1()==null || user.getPassword1().isEmpty() || !user.getPassword1().equals(user.getPassword2())){
			return Response.status(Status.BAD_REQUEST).entity("password invalid").build();
		}else{
			user.setPassword1(BCrypt.hashpw(user.getPassword1(), BCrypt.gensalt()));
		}
		
		UUID randomUUID = UUID.randomUUID();
		userDAO.createUser(user, randomUUID);
		user.setId(randomUUID);
		user.clearPAsswords();
		return Response.ok().entity(user).build();
	}

	private boolean verifyRecapchaResponse(String gRecaptchaResponse) {
		if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
            return false;
        }
         
        try{
	        URL obj = new URL(url);
	        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	 
	        // add reuqest header
	        con.setRequestMethod("POST");
	        con.setRequestProperty("User-Agent", USER_AGENT);
	        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 
	        String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;
	 
	        // Send post request
	        con.setDoOutput(true);
	        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	        wr.writeBytes(postParams);
	        wr.flush();
	        wr.close();
	 
	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	 
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	         
	        //parse JSON response and return 'success' value
	        JsonNode jsonObject = MAPPER.readTree(response.toString());
	         
	        return jsonObject.get("success").asBoolean();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
	}

//	private Response returnSuccess(User user) {
//		return Response.ok().header("Access-Control-Allow-Origin", getCrossDomainString()).entity(user).build();
//		//return Response.ok().entity(user).build();
//	}
//
//	private Response returnError(Status status, String message) {
//		return Response.status(status).header("Access-Control-Allow-Origin", getCrossDomainString()).entity(message).build();
//		//return Response.status(status).entity(message).build();
//	}
//
//	private String getCrossDomainString() {
//		return "*";
//	}
	
	
}
