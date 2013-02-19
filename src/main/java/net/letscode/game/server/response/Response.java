package net.letscode.game.server.response;

import net.letscode.game.server.ClientSession;

/**
 * Represents a response to be sent to a request. In this case 
 * @author timothyb89
 */
public class Response {
	
	public static final String JSON_TYPE = "response";
	
	private ClientSession session;
	private String requestId;
	
}
