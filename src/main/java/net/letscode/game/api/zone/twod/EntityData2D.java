package net.letscode.game.api.zone.twod;

import lombok.Data;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.misc.QuadTreeItem;

/**
 * Defines the container for properties entities need to exist within 2D space.
 * Specifically, this provides position, heading, and velocity data for a
 * particular entity.
 * @author timothyb89
 */
@Data
public class EntityData2D implements QuadTreeItem {
	
	private Entity entity;
	private double x;
	private double y;
	private double heading;
	private double velocity;
	
}
