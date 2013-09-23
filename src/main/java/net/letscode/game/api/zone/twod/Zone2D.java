package net.letscode.game.api.zone.twod;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.misc.QuadTree;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.misc.Boundry2D;
import net.letscode.game.misc.Point;

/**
 * Defines a Zone in which entities are placed in a continuous 2d plane. 
 * @author timothyb89
 */
@Slf4j
public class Zone2D extends Zone {
	
	@Getter
	private Boundry2D bounds;
	private Map<Entity, EntityData2D> dataMap;
	private QuadTree<EntityData2D> entityMap;
	
	public Zone2D(Boundry2D bounds, int treeDepth) {
		this.bounds = bounds;
		
		dataMap = new HashMap<>();
		entityMap = new QuadTree<>(bounds, treeDepth);
	}
	
	public Zone2D(double x, double y, double width, double height, int treeDepth) {
		this(new Boundry2D(x, y, width, height), treeDepth);
	}
	
	public Zone2D(double width, double height, int treeDepth) {
		this(new Boundry2D(0, 0, width, height), treeDepth);
	}
	
	public Zone2D() {
		this(100, 100, 7); // accuracy is roughly ~0.78
	}

	/**
	 * Adds the entity to the current zone. In addition to the actions performed
	 * by {@code Zone}'s {@code addEntity()}, this also creates an
	 * {@link EntityData2D} instance and appends it to the entity map.
	 * @see Zone#addEntity(Entity) 
	 * @param entity the entity to add
	 */
	@Override
	public void addEntity(Entity entity) {
		super.addEntity(entity);
		
		EntityData2D data = new EntityData2D(entity);
		dataMap.put(entity, data);
		
		entityMap.insert(data);
	}

	@Override
	public void removeEntity(Entity entity) {
		super.removeEntity(entity);
		
		EntityData2D data = dataMap.get(entity);
		if (data == null) {
			log.warn("Attempted to remove entity from 2D zone "
					+ "that was not in zone.");
		} else {
			dataMap.remove(entity);
			entityMap.remove(data);
		}
	}
	
	/**
	 * Moves the specified entity to the given position within this zone.
	 * @param e the entity to move
	 * @param pos the new location for the entity
	 */
	public void move(Entity e, Point pos) {
		EntityData2D data = dataMap.get(e);
		if (data == null) {
			throw new IllegalArgumentException("Attempted to move entity not "
					+ "currently in this zone.");
		}
		
		Point start = data.getPosition();
		data.setPosition(pos);
		
		entityMap.update(data);
		
		bus.push(new Zone2DMovementEvent(data, start, pos));
	}
	
}
