package net.letscode.game.api.zone.twod;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.event.Event;
import net.letscode.game.misc.Point2D;

/**
 *
 * @author timothyb
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ToString
public class Zone2DMovementTickEvent extends Event {
	
	private final Zone2D zone;
	private final Entity entity;
	
	/**
	 * The entity data for the zone. This includes current information about the
	 * movement event, such as the heading and velocity.
	 */
	private final EntityData2D data;
	
	/**
	 * The starting location of the entity movement
	 */
	private final Point2D location;
	
}
