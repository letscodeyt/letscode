package net.letscode.game.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.letscode.game.event.Event;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MessageDispatcher;

/**
 * Defines parameters for an IncomingMessageEvent. Incoming message events are
 * fired when the server receives a message from the client. Note that these
 * events will only trigger for "raw" message; that is, before any parsing has
 * occurred. Parsed messages will instead be distributed through the event bus
 * of {@link MessageDispatcher}.
 * @author timothyb89
 */
@Data
public class IncomingMessageEvent extends Event {
	
	/**
	 * The client from which the message originated.
	 */
	private final ClientSession client;
	
	/**
	 * The message data from the client.
	 */
	private final JsonNode message;
	
}
