package com.lostVictories.resources;

import static com.lostVictories.resources.authenticated.GamesResource.MAPPER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.lostVictories.dao.UserDAO;
import com.lostVictories.model.User;
import com.lostVictories.resources.exceptions.InvalidRequestException;

@RestController
@RequestMapping("/createUser")
public class CreateUserResource {

	public static final String url = "https://www.google.com/recaptcha/api/siteverify";
    public static final String secret = "6LeQ0xUTAAAAAIfp98rXXBJMa_43xUX7EMO2YNST";
    private final static String USER_AGENT = "Mozilla/5.0";
	
	private UserDAO userDAO;
	
	@Autowired
    public CreateUserResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
    
	@RequestMapping(path="", method=POST, consumes=APPLICATION_JSON_VALUE, produces=APPLICATION_JSON_VALUE)
	public User createUser(@RequestBody User user, HttpServletRequest request) throws ServletException{
		boolean recapchaResponse = verifyRecapchaResponse(user.getRecaptchaResponse());
		
		if(!recapchaResponse){
			throw new InvalidRequestException("Capcha failed!");			
		}
		if(null==user.getUsername() || userDAO.existsUsername(user.getUsername())){
			throw new InvalidRequestException("username or password invalid");			
		}
		if(userDAO.existsEmail(user.getEmail())){
			throw new InvalidRequestException("email already registered");
		}
		if(user.getPassword1()==null || user.getPassword1().isEmpty() || !user.getPassword1().equals(user.getPassword2())){
			throw new InvalidRequestException("username or password invalid");
		}else{
			user.setPassword1(BCrypt.hashpw(user.getPassword1(), BCrypt.gensalt()));
		}
		
		UUID randomUUID = UUID.randomUUID();
		userDAO.createUser(user, randomUUID);
		user.setId(randomUUID);
		request.login(user.getUsername(), user.getPassword2());
		return user.clearPAsswords();
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
