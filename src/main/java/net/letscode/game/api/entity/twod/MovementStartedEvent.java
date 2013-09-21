package net.letscode.game.api.entity.twod;

import net.letscode.game.api.zone.Zone;
import net.letscode.game.api.zone.twod.Zone2D;
import net.letscode.game.event.ContextualEvent;

/**
 * An event that occurs when an entity has started moving within a 2d plane.
 * These events expose the entity's zone, initial position, velocity, 
 * @author tim
 */
public class MovementStartedEvent extends ContextualEvent<Entity2D> {

	private final Zone2D zone;
	private final double x;
	private final double y;
	private final double heading;
	private final double velocity;
	
	public MovementStartedEvent(Entity2D context,
			Zone2D zone, double x, double y, double heading, double velocity) {
		super(context);
		
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.heading = heading;
		this.velocity = velocity;
	}
	
	
	
}
