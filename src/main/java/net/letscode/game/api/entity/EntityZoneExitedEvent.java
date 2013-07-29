package net.letscode.game.api.entity;

import lombok.Getter;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.event.ContextualEvent;

/**
 * Defines an EntityZoneExitedEvent. This particular type of event is fired
 * when a particular {@link Entity} exits from a {@link Zone}. This is different
 * from {@link net.letscode.game.api.zone.ZoneExitedEvent} in that it is
 * Entity-scoped; that is, it allows client classes to listen for all zone
 * events that occur for a particular {@code Entity}, rather than within a
 * particular {@code Zone}.
 * <p>This is an extension {@link ContextualEvent}; as such, the context for
 * this event is the {@link Entity} rather than a zone as in the case of
 * {@code ZoneExitedEvent}.</p>
 * @author timothyb89
 */
public class EntityZoneExitedEvent extends ContextualEvent<Entity> {
	
	/**
	 * The zone that was exited.
	 */
	@Getter
	private Zone zone;

	public EntityZoneExitedEvent(Entity context, Zone zone) {
		super(context);
		
		this.zone = zone;
	}
	
}
