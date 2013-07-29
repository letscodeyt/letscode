package net.letscode.game.api.zone;

import lombok.Getter;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.event.ContextualEvent;

/**
 * Defines a ZoneExitEvent. Zone exit events are fired when an entity has left
 * a {@link Zone}. This event extends {@link ContextualEvent}; as such, the 
 * context for this event is the zone that the entity exited.
 * @author timothyb89
 */
public class ZoneExitedEvent extends ContextualEvent<Zone> {
	
	/**
	 * The entity that exited the zone.
	 */
	@Getter
	private final Entity entity;

	public ZoneExitedEvent(Zone context, Entity entity) {
		super(context);
		
		this.entity = entity;
	}
	
}
