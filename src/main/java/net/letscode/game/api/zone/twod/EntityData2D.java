package net.letscode.game.api.zone.twod;

import net.letscode.game.misc.QuadTreeItem;
import lombok.Data;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.misc.Point2D;
import net.letscode.game.misc.QuadTree;

/**
 * Defines the container for properties entities need to exist within 2D space.
 * Specifically, this provides position, heading, and velocity data for a
 * particular entity.
 * @author timothyb89
 */
@Data
public class EntityData2D implements QuadTreeItem {
	
	private Entity entity;
	private Point2D position; // bounds are part of the View
	private double heading;
	private double velocity;

	private QuadTree parent;
	
	public EntityData2D(Entity entity) {
		this.entity = entity;
	}
	
	@Override
	public QuadTree getQuadTreeParent() {
		return parent;
	}

	@Override
	public void setQuadTreeParent(QuadTree parent) {
		this.parent = parent;
	}

	@Override
	public double getX() {
		return position.y;
	}

	@Override
	public double getY() {
		return position.x;
	}
	
}
