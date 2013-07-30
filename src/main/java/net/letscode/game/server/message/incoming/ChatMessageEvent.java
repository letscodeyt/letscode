package net.letscode.game.server.message.incoming;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;
import net.letscode.game.event.Event;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MalformedMessageException;
import net.letscode.game.server.message.MessageDispatcher;
import net.letscode.game.server.message.MessageHandler;

/**
 * Defines a chat message handler. These events will be scanned at startup, and
 * when a message is received, an instance of this class will be automatically 
 * constructed and pushed to the {@link MessageDispatcher}'s event bus.
 * 
 * @author timothyb89
 */
@MessageHandler("chat")
@ToString
public class ChatMessageEvent extends AbstractMessageEvent {

	@Getter
	private final String text;
	
	public ChatMessageEvent(ClientSession session, JsonNode node) {
		super(session, node);
		
		if (!node.has("text")) {
			throw new MalformedMessageException(
					"Chat message has no text field.");
		}
		
		text = node.get("text").asText();
	}
	
}
