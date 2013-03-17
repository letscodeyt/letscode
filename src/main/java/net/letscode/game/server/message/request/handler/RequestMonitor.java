package net.letscode.game.server.message.request.handler;

import com.fasterxml.jackson.databind.JsonNode;
import net.letscode.game.server.ClientSession;
import net.letscode.game.server.JsonSerializable;
import net.letscode.game.server.SessionListener;
import net.letscode.game.server.message.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitors outgoing messages for Request objects. If an outgoing Request
 * instance is found, it will be registered in the {@link RequestRegistry}.
 * @author timothyb89
 */
public class RequestMonitor implements SessionListener {

	private Logger logger = LoggerFactory.getLogger(RequestMonitor.class);
	
	@Override
	public void onMessageSent(ClientSession client, JsonSerializable s) {
		if (s instanceof Request) {
			Request request = (Request) s;
			
			logger.info("Registered outgoing request: " + request);
			
			RequestRegistry.get().register(request);
		}
	}

	@Override
	public void onMessageReceived(ClientSession client, JsonNode node) {
		// do nothing
	}
	
}
