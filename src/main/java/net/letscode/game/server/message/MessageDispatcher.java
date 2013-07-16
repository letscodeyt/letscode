package net.letscode.game.server.message;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Constructor;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.event.EventHandler;
import net.letscode.game.server.ClientSession;
import net.letscode.game.server.message.event.IncomingMessageEvent;

/**
 * A {@code SessionListener} that handles the dispatching of all incoming
 * messages from a given client. Messages will be dispatched to registered
 * {@link MessageHandler}s via their {@code type} field
 * @author timothyb89
 */
@Slf4j
public class MessageDispatcher {

	@EventHandler
	public void onMessageReceived(IncomingMessageEvent event) {
		JsonNode node = event.getMessage();
		
		// check the message type
		if (!node.has("type")) {
			log.warn("Message has no defined type: " + node);
			// TODO: notify the client?
			return;
		}
		
		String type = node.get("type").asText();
		
		Class<?> clazz = MessageHandlerFactory.get().getHandler(type);
		if (clazz == null) {
			log.warn("No handler registered for type " + type);
			return;
		}
		
		try {
			Constructor c = clazz.getConstructor(
					ClientSession.class, JsonNode.class);
			c.newInstance(event.getClient(), node); // ignore the actual instance
		} catch (NoSuchMethodException ex) {
			log.error("Class " + clazz + " has no (ClientSession, JsonNode) "
					+ "constructor", ex);
		} catch (Exception ex) {
			log.error("Could not initialize MessageHandler: " + clazz, ex);
		}
	}
	
}
