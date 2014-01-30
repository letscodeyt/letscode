package net.letscode.game.api.world;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.event.Event;

/**
 * An event dispatched when a {@link Zone} has been removed from the
 * {@link World}.
 * @author timothyb
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ToString
public class ZoneRemovedEvent extends Event {
	
	/**
	 * The zone that was removed. Classes should take care not to keep
	 * references to the zone in memory to ensure garbage collection can take
	 * place.
	 */
	private final Zone zone;
	
}
