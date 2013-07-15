package net.letscode.game.server.message;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Constructor;
import net.letscode.game.api.util.JsonSerializable;
import net.letscode.game.server.ClientSession;
import net.letscode.game.server.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@code SessionListener} that handles the dispatching of all incoming
 * messages from a given client. Messages will be dispatched to registered
 * {@link MessageHandler}s via their {@code type} field
 * @author timothyb89
 */
public class MessageDispatcher implements SessionListener {

	private Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
	
	@Override
	public void onMessageSent(ClientSession client, JsonSerializable s) {
		// ignore
	}

	@Override
	public void onMessageReceived(ClientSession client, JsonNode node) {
		// check the message type
		if (!node.has("type")) {
			logger.warn("Message has no defined type: " + node);
			// TODO: notify the client?
			return;
		}
		
		String type = node.get("type").asText();
		
		Class<?> clazz = MessageHandlerFactory.get().getHandler(type);
		if (clazz == null) {
			logger.warn("No handler registered for type " + type);
			return;
		}
		
		try {
			Constructor c = clazz.getConstructor(
					ClientSession.class, JsonNode.class);
			c.newInstance(client, node); // ignore the actual instance
		} catch (NoSuchMethodException ex) {
			logger.error("Class " + clazz + " has no (ClientSession, JsonNode) "
					+ "constructor", ex);
		} catch (Exception ex) {
			logger.error("Could not initialize MessageHandler: " + clazz, ex);
		}
	}
	
}
