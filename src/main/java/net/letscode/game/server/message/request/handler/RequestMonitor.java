package net.letscode.game.server.message.request.handler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.util.JsonSerializable;
import net.letscode.game.server.ClientSession;
import net.letscode.game.server.SessionListener;
import net.letscode.game.server.message.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitors outgoing messages for Request objects. If an outgoing Request
 * instance is found, it will be registered in the {@link RequestRegistry}.
 * @author timothyb89
 */
@Slf4j
public class RequestMonitor implements SessionListener {

	@Override
	public void onMessageSent(ClientSession client, JsonSerializable s) {
		if (s instanceof Request) {
			Request request = (Request) s;
			
			log.info("Registered outgoing request: " + request);
			
			RequestRegistry.get().register(request);
		}
	}

	@Override
	public void onMessageReceived(ClientSession client, JsonNode node) {
		// do nothing
	}
	
}
