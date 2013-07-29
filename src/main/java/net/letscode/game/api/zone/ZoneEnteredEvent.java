package net.letscode.game.api.zone;

import lombok.Getter;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.event.ContextualEvent;

/**
 * Defines parameters for a ZoneEnteredEvent. Zone entry events are fired after
 * an entity is added to or otherwise enters a {@link Zone}. This event extends
 * {@link ContextualEvent} and as such, the context for the event is the 
 * @author timothyb89
 */
public class ZoneEnteredEvent extends ContextualEvent<Zone> {
	
	/**
	 * The entity that entered the zone
	 */
	@Getter
	private final Entity entity;

	public ZoneEnteredEvent(Zone context, Entity entity) {
		super(context);
		
		this.entity = entity;
	}
	
}
