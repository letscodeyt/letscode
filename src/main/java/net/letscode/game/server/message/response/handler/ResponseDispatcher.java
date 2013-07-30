package net.letscode.game.server.message.response.handler;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Constructor;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MessageHandler;

/**
 * Handles the dispatching of incoming requests to their required
 * {@link Response}. Incoming requests are matched to their local types via
 * the {@code name} property. The {@link ResponseHandlerFactory} then handles
 * the mapping of type names to actual server-side classes.
 * @author timothyb89
 */
@Slf4j
@MessageHandler("request")
public class ResponseDispatcher {
	
	/**
	 * Constructs a ResponseDispatcher. This constructor will automatically be
	 * called by the {@link MessageDispatcher}. This will attempt to relay the
	 * message to a {@link ResponseHandler} that was registered in the
	 * {@link ResponseHandlerFactory}.
	 * @param session the client session in which the message was received
	 * @param message the message that was received
	 */
	public ResponseDispatcher(ClientSession session, JsonNode node) {
		// check for a 'name' field
		if (!node.has("name")) {
			log.warn("Invalid request from client: " + node);
			// TODO: notify the client of the error?
			return;
		}
		
		// check for an id field - if there's no id, we won't be able to send
		// a response, so why bother creating one?
		if (!node.has("id")) {
			log.warn("Invalid request from client is missing id: " + node);
			return;
		}
		
		String name = node.get("name").asText();
		
		// query the factory to get the class
		Class<?> clazz = ResponseHandlerFactory.get().getHandler(name);
		if (clazz == null) {
			log.warn("Invalid request name: " + name);
			return;
		}
		
		try {
			Constructor c = clazz.getConstructor(
					ClientSession.class, JsonNode.class);
			c.newInstance(session, node); // we can ignore the return value
		} catch (Exception ex) {
			log.error("Missing (ClientSession, JsonNode) constructor in: "
					+ clazz);
		}
	}
	
}
