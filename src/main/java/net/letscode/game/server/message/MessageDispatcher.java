package net.letscode.game.server.message;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Constructor;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.event.EventBus;
import net.letscode.game.event.EventBusClient;
import net.letscode.game.event.EventBusProvider;
import net.letscode.game.event.EventHandler;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.client.IncomingMessageEvent;
import net.letscode.game.server.message.incoming.AbstractMessageEvent;
import net.letscode.game.server.message.incoming.ChatMessageEvent;
import net.letscode.game.server.message.incoming.LoginMessageEvent;
import net.letscode.game.server.message.incoming.RegistrationMessageEvent;

/**
 * A {@code SessionListener} that handles the dispatching of all incoming
 * messages from a given client. Messages will be dispatched to registered
 * {@link MessageHandler}s via their {@code type} field
 * @author timothyb89
 */
@Slf4j
public class MessageDispatcher implements EventBusProvider {

	private EventBus bus;
	
	public MessageDispatcher() {
		// TODO: MessageHandlerFactory already scans these - it would be easy to
		// make a list of Event classes and add them here automatically
		
		bus = new EventBus() {{
			add(ChatMessageEvent.class);
			add(LoginMessageEvent.class);
			add(RegistrationMessageEvent.class);
		}};
	}
	
	@EventHandler
	public void onMessageReceived(IncomingMessageEvent event) {
		log.info("Attempting to dispatch new message: " + event);
		
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
			Object o = c.newInstance(event.getClient(), node);
			
			// if the object is an Event instance, push it to the event bus
			if (o instanceof AbstractMessageEvent) {
				AbstractMessageEvent ame = (AbstractMessageEvent) o;
				bus.push(ame);
			}
		} catch (NoSuchMethodException ex) {
			log.error("Class " + clazz + " has no (ClientSession, JsonNode) "
					+ "constructor", ex);
		} catch (MalformedMessageException ex) {
			log.error("Client message was malformed", ex);
			// TODO: possibly tell the client?
		} catch (Exception ex) {
			log.error("Could not initialize MessageHandler: " + clazz, ex);
		}
	}

	@Override
	public EventBusClient bus() {
		return bus.getClient();
	}
	
}
