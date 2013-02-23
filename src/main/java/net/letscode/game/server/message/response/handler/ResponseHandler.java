package net.letscode.game.server.message.response.handler;

import net.letscode.game.server.ClientSession;

/**
 * Represents a response sent from the client.
 * @author timothyb89
 */

public class ResponseHandler {
	
	public static final String JSON_TYPE = "response";
	
	private ClientSession session;
	private String requestId;
	
}
