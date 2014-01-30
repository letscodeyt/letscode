package net.letscode.game.api.world;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.letscode.game.event.Event;

/**
 * An event dispatched when a new tick or iteration of game loop has started.
 * Zones and entities may use this event to process events over a period of
 * time.
 * @author timothyb
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString

public class WorldTickEvent extends Event {
	
	/**
	 * The timestamp of the current tick. This is not necessarily the time that
	 * the current event listener is notified as other (potentially higher
	 * priority) events may have been processed first.
	 */
	private final long timestamp;
	
	/**
	 * The elapsed time since the last tick occurred.
	 */
	private final long elapsed;
	
	/**
	 * The current tick rate, in milliseconds.
	 */
	private final long tickRate;
	
	/**
	 * The expected ending timestamp for the current game tick.
	 */
	private final long deadline;
	
}
