package net.letscode.game.api.zone;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.util.TargetedSerializable;
import net.letscode.game.event.EventBus;
import net.letscode.game.event.EventBusClient;
import net.letscode.game.event.EventBusProvider;

/**
 * A zone is an abstract collection of entities. 
 * <p>Zones can be extended to provide additional functionality; see the
 * additional classes within this package for examples.</p>
 * <p>In addition to </p>
 * @author timothyb89
 */
public class Zone implements TargetedSerializable<Entity>, EventBusProvider {
	
	/**
	 * The {@link EventBus} for this Zone. This should be used to push new
	 * events to registered listener classes. Note that all event types will
	 * need to be registered with the {@code EventBus}; this may be done by
	 * subclasses by calling {@link EventBus#add(Class)}.
	 */
	protected EventBus bus;
	
	/**
	 * The list of entities contained in this zone. Note that this does not
	 * include entities within child zones.
	 * TODO: possibly use something like a LinkedHashSet here to speed up calls
	 * to contains(). Iteration would remain O(n) but contains() would be O(1)
	 * for a nicely-dispersed list.
	 */
	protected List<Entity> entities; 
	
	public Zone() {
		bus = new EventBus() {{
			add(ZoneEnteredEvent.class);
			add(ZoneExitedEvent.class);
		}};
		
		entities = new LinkedList<>();
	}
	
	@Override
	public EventBusClient bus() {
		return bus.getClient();
	}
	
	//
	// Entity management specifics
	//
	
	/**
	 * Adds the given entity to this zone. This will fire a
	 * {@link ZoneEnteredEvent} which may be extended by subclasses to provide
	 * additional information. Note that as an entity may join multiple zones
	 * at a time, this will not have a direct affect on any other zones the
	 * entity may happen to be a member of.
	 * <p>Note that all zone addition and removal logic resides in this class;
	 * any methods that may exist in {@link Entity} or {@link World} are
	 * essentially wrappers around this method unless other functionality is
	 * otherwise noted.</p>
	 * @see Zone#removeEntity(Entity) 
	 * @see Zone#fireZoneEnteredEvent(Entity) 
	 * @param entity the entity to add to the zone
	 */
	public void addEntity(Entity entity) {
		entities.add(entity);
		entity._enteredZone(this);
		
		// TODO: make sure the entity starts receiving zone events
		
		fireZoneEnteredEvent(entity);
	}
	
	/**
	 * Removes the given entity from this zone. This will fire a
	 * {@link ZoneExitedEvent}, though it may be extended by subclasses to
	 * provide additional information.
	 * <p>Note that all zone addition and removal logic resides in this class;
	 * any methods that may exist in {@link Entity} or {@link World} are
	 * essentially wrappers around this method unless other functionality is
	 * otherwise noted.</p>
	 * @see Zone#addEntity(Entity) 
	 * @see Zone#fireZoneExitedEvent(Entity)
	 * @param entity the entity to remove from the zone
	 */
	public void removeEntity(Entity entity) {
		entities.remove(entity);
		entity._exitedZone(this);
		
		// TODO: this will fire an event regardless of whether or not the entity
		// is actually in the zone - should this be intended?
		// a call to contains() could be relatively costly, so we should try to
		// avoid it if not doing so will avoid issues
		// alternatively:
		// - use a LinkedHashSet
		// - do a reverse check, if entity.isInZone(this) or some such, as the
		//   entity's own list should be much smaller.
		
		// TODO: make sure the entity stops receiving zone events
		
		fireZoneEnteredEvent(entity);
	}
	
	/**
	 * This fires a {@link ZoneEnteredEvent}. This may be overridden by
	 * subclasses should they wish to modify the event or event parameters.
	 * @see Zone#addEntity(Entity)
	 * @param entity the entity that entered the zone
	 */
	protected void fireZoneEnteredEvent(Entity entity) {
		bus.push(new ZoneEnteredEvent(this, entity));
	}
	
	/**
	 * This fires a {@link ZoneExitedEvent}, and is called by
	 * {@link #removeEntity(Entity)} when an entity is removed from the zone.
	 * This may be overridden by subclasses should they wish to modify the
	 * event or event parameters.
	 * @param entity The entity removed from the zone
	 */
	protected void fireZoneExitedEvent(Entity entity) {
		bus.push(new ZoneExitedEvent(this, entity));
	}
	
	/**
	 * Checks if this zone contains the given entity. Note that this does not
	 * check child zones for the entity.
	 * @param entity the entity to check for
	 * @return true if the zone contains the given entity; false if not
	 */
	public boolean containsEntity(Entity entity) {
		return entities.contains(entity);
	}
	
	//
	// serialization specifics
	//
	
	/**
	 * Serializes the list of entities for this zone into a JSON array. Note
	 * that this simply produces a field value and not an actual field, and is
	 * suitable for callers to use as a field value after calling
	 * {@link JsonGenerator#writeFieldName(String)}.
	 * @param g the JsonGenerator to use
	 */
	protected void serializeEntities(JsonGenerator g, Entity target)
			throws IOException {
		g.writeArrayFieldStart("entities");
		
		// check target; if null, don't call serializeFor() for entities
		if (target == null) {
			for (Entity e : entities) {
				e.serialize(g);
			}
		} else {
			for (Entity e : entities) {
				e.serializeFor(g, target);
			}
		}
		
		g.writeEndArray();
	}
	
	/**
	 * Serializes the entire zone; that is, all entities within the zone.
	 * Subclasses may wish to override this method to add additional fields.
	 * @param g the {@link JsonGenerator} to write the zone data to.
	 * @throws IOException 
	 */
	@Override
	public void serialize(JsonGenerator g) throws IOException {
		serializeFor(g, null);
	}
	
	@Override
	public void serializeFor(JsonGenerator g, Entity target) throws IOException {
		g.writeStartObject();
		serializeEntities(g, target);
		g.writeEndObject();
	}
	
}
