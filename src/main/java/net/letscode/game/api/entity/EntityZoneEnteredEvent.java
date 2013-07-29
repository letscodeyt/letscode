package net.letscode.game.api.entity;

import lombok.Getter;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.event.ContextualEvent;

/**
 * Defines an EntityZoneEnteredEvent. This particular type of event is fired
 * when a particular {@link Entity} enters a {@link Zone}. This is different
 * from {@link net.letscode.game.api.zone.ZoneEnteredEvent} in that it is
 * Entity-scoped; that is, it allows client classes to listen for all zone
 * events that occur for a particular {@code Entity}, rather than within a
 * particular {@code Zone}.
 * <p>This is an extension {@link ContextualEvent}; as such, the context for
 * this event is the {@link Entity} rather than a zone as in the case of
 * {@code ZoneEnteredEvent}.</p>
 * @author timothyb89
 */
public class EntityZoneEnteredEvent extends ContextualEvent<Entity> {
	
	/**
	 * The zone that was joined.
	 */
	@Getter
	private final Zone zone;

	public EntityZoneEnteredEvent(Entity context, Zone zone) {
		super(context);
		
		this.zone = zone;
	}
	
}
