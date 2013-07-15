package net.letscode.game.server.message.request.handler;

import java.util.HashMap;
import java.util.Map;
import net.letscode.game.server.message.request.Request;

/**
 * Handles storage and management of Request instances stored and awaiting a
 * response from the client. 
 * @author timothyb89
 */
public class RequestRegistry {
	
	private static RequestRegistry instance;
	
	private Map<String, Request> requests;
	
	private RequestRegistry() {
		requests = new HashMap<>();
	}
	
	public static RequestRegistry get() {
		if (instance == null) {
			instance = new RequestRegistry();
		}
		
		return instance;
	}
	
	public void register(Request request) {
		requests.put(request.getId(), request);
	}
	
	public Request get(String id) {
		return requests.get(id);
	}
	
}
