package net.letscode.game.api.world;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.event.Event;

/**
 * An event dispatched when a {@link Zone} has been added to the {@link World}.
 * @author timothyb
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ToString
public class ZoneAddedEvent extends Event {
	
	/**
	 * The zone that was added to the world.
	 */
	private final Zone zone;
	
}
