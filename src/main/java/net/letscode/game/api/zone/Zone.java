package net.letscode.game.api.zone;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
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
 * @author timothyb89
 */
public class Zone implements TargetedSerializable<Entity>, EventBusProvider {
	
	protected EventBus bus;
	
	/**
	 * The list of entities contained in this zone.
	 */
	private List<Entity> entities; 
	
	public Zone() {
		bus = new EventBus();
		entities = new LinkedList<>();
	}
	
	@Override
	public EventBusClient bus() {
		return bus.getClient();
	}
	
	/**
	 * Adds the given entity to this zone.
	 * @param entity 
	 */
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	/**
	 * Removes the given entity from this zone. 
	 * @param entity 
	 */
	public void removeEntity(Entity entity) {
		entities.remove(entity);
	}
	
	/**
	 * Checks if this zone contains the given entity. 
	 * @param entity
	 * @return 
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
	 * @param g
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
