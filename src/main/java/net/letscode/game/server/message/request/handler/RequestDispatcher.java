package net.letscode.game.server.message.request.handler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MessageHandler;
import net.letscode.game.server.message.request.Request;

/**
 * A {@link MessageHandler} that manages the dispatching of incoming responses
 * to {@link Request} classes registered in the {@link RequestHandlerFactory}
 * @see RequestHandlerFactory
 * @author timothyb89
 */
@Slf4j
@MessageHandler("response")
public class RequestDispatcher {
	
	/**
	 * Constructs a RequestDispatcher. This constructor will automatically be
	 * called by the {@link MessageDispatcher}. This will attempt to relay the
	 * message to a {@link Request} that was created with the same {@code id}.
	 * @param session the client session in which the message was received
	 * @param message the message that was received
	 */
	public RequestDispatcher(ClientSession session, JsonNode message) {
		// check for an id field
		if (!message.has("id")) {
			log.warn("Client response missing id: " + message);
			// TODO: notify the client of the error?
			return;
		}
		
		String id = message.get("id").asText();
		
		// query the RequestRegistry
		Request r = RequestRegistry.get().get(id);
		if (r == null) {
			log.warn("No request found with id " + id + ": " + message);
			return;
		}
		
		// process the response
		r.notify(message);
	}
	
}
