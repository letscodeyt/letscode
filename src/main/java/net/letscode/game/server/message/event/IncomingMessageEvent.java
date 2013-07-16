package net.letscode.game.server.message.event;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.letscode.game.event.Event;
import net.letscode.game.server.ClientSession;

/**
 * Defines parameters for an IncomingMessageEvent. Incoming message events are
 * fired when the server receives a message from the client.
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
