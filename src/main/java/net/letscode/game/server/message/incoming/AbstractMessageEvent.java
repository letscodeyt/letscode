package net.letscode.game.server.message.incoming;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import net.letscode.game.event.Event;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MessageDispatcher;

/**
 * Defines an abstract message event, specifically intended for events that
 * trigger on reception of a message, and that are constructed automatically
 * via {@link MessageDispatcher}.
 * @author timothyb89
 */
public abstract class AbstractMessageEvent extends Event {
	
	@Getter
	private final ClientSession session;
	
	@Getter
	private final JsonNode node;

	public AbstractMessageEvent(ClientSession session, JsonNode node) {
		this.session = session;
		this.node = node;
	}
	
}
