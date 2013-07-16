package net.letscode.game.api.zone;

import lombok.Getter;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.event.ContextualEvent;

/**
 * Defines parameters for a ZoneEntryEvent. Zone entry events are fired when an
 * entity is added to or otherwise enters a {@link Zone}
 * @author timothyb89
 */
public class ZoneEntryEvent extends ContextualEvent<Zone> {
	
	/**
	 * The zone in which the event occurred.
	 */
	@Getter
	private final Zone zone;
	
	/**
	 * The entity that entered the zone
	 */
	@Getter
	private final Entity entity;

	public ZoneEntryEvent(Zone zone, Entity entity, Zone context) {
		super(context);
		this.zone = zone;
		this.entity = entity;
	}
	
}
