package net.letscode.game.api.zone.chat;

import lombok.Getter;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.api.zone.ZoneExitedEvent;

/**
 * Defines a ChatZoneExitedEvent. This event is a simple extension of
 * {@link ZoneExitedEvent} that adds support for an IRC-like quit message.
 * @author timothyb89
 */
public class ChatZoneExitedEvent extends ZoneExitedEvent {
	
	/**
	 * The "quit message" associated with this event, possibly null.
	 */
	@Getter
	private final String message;

	public ChatZoneExitedEvent(Zone context, Entity entity) {
		super(context, entity);
		
		message = null;
	}

	public ChatZoneExitedEvent(Zone context, Entity entity, String message) {
		super(context, entity);
		
		this.message = message;
	}
	
}
