package net.letscode.game.api.zone;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.List;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.util.TargetedSerializable;

/**
 * A zone is an abstract collection of entities. 
 * <p>Zones can be extended to provide additional functionality; see the
 * additional classes within this package for examples.</p>
 * @author timothyb89
 */
public class Zone implements TargetedSerializable<Entity> {
	
	private List<Entity> entities; 

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
		
		// check t
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
