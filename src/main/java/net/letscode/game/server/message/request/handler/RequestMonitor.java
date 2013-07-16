package net.letscode.game.server.message.request.handler;

import lombok.extern.slf4j.Slf4j;
import net.letscode.game.event.EventHandler;
import net.letscode.game.server.message.event.OutgoingMessageEvent;
import net.letscode.game.server.message.request.Request;

/**
 * Monitors outgoing messages for Request objects. If an outgoing Request
 * instance is found, it will be registered in the {@link RequestRegistry}.
 * @author timothyb89
 */
@Slf4j
public class RequestMonitor {

	@EventHandler
	public void onMessageSent(OutgoingMessageEvent event) {
		if (event.getMessage() instanceof Request) {
			Request request = (Request) event.getMessage();
			
			log.info("Registered outgoing request: " + request);
			
			RequestRegistry.get().register(request);
		}
	}
	
}
