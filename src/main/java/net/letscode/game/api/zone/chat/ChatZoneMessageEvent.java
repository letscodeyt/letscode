package net.letscode.game.api.zone.chat;

import lombok.Getter;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.event.ContextualEvent;

/**
 * Defines a basic chat message event. Chat message events are fired whenever an
 * entity has sent a message to the zone of interest.
 * @author timothyb89
 */
public class ChatZoneMessageEvent extends ContextualEvent<ChatZone> {
	
	/**
	 * The actual text of the message that was sent.
	 */
	@Getter
	private final ChatMessage message;

	public ChatZoneMessageEvent(ChatZone context, ChatMessage message) {
		super(context);
		
		this.message = message;
	}
	
	
	
}
