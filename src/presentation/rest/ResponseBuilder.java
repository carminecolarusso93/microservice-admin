package presentation.rest;

import javax.ws.rs.core.Response;

public class ResponseBuilder {
	
	public static Response createOkResponse(Object response) {
	return 	Response.status(200)
	    .header("Access-Control-Allow-Origin", "*")
	    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	    .header("Access-Control-Allow-Credentials", "true")
	    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	    .header("Access-Control-Max-Age", "1209600")
	    .entity(response)
	    .build();
	}
	
	public static Response createNotFoundResponse() {
		return 	Response.status(Response.Status.NOT_FOUND)
		    .header("Access-Control-Allow-Origin", "*")
		    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		    .header("Access-Control-Allow-Credentials", "true")
		    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		    .header("Access-Control-Max-Age", "1209600") 
		    .build();
		}
	
	
}
