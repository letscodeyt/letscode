package net.letscode.game.server.message.outgoing;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.letscode.game.api.zone.chat.ChatZoneMessage;

/**
 *
 * @author timothyb89
 */
public class ChatMessage extends OutgoingMessage {

	private final ChatZoneMessage message;
	
	public ChatMessage(ChatZoneMessage message) {
		super("chat");
		
		this.message = message;
	}
	
	@Override
	protected void serializeSubclass(JsonGenerator g) throws IOException {
		g.writeStringField("chat-type", message.getType());
		g.writeStringField("text", message.getText());
		
		// TODO: some sender ID
	}
	
}
