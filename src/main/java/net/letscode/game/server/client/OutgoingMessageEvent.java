package net.letscode.game.server.client;

import lombok.Data;
import net.letscode.game.api.util.JsonSerializable;
import net.letscode.game.event.Event;
import net.letscode.game.server.client.ClientSession;

/**
 * Defines parameters for an OutgoingMessageEvent. Outgoing message events are
 * triggered after the server has sent a message to the client.
 * @author timothyb89
 */
@Data
public class OutgoingMessageEvent extends Event {
	
	private final ClientSession session;
	private final JsonSerializable message;
	
}
