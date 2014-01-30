package net.letscode.game.api.zone.twod;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.letscode.game.event.Event;
import net.letscode.game.misc.Point2D;

/**
 *
 * @author timothyb
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Zone2DMovementEvent extends Event {
	
	private final EntityData2D entity;
	private final Point2D start;
	private final Point2D end;
	
	public Zone2DMovementEvent(EntityData2D entity, Point2D start, Point2D end) {
		this.entity = entity;
		this.start = start;
		this.end = end;
	}
	
}
