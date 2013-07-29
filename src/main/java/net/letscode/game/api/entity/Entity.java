package net.letscode.game.api.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import net.letscode.game.api.util.TargetedSerializable;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.event.EventBus;
import net.letscode.game.event.EventBusClient;
import net.letscode.game.event.EventBusProvider;

/**
 * Defines a basic entity. An entity is essentially some server-side object,
 * unique only in that it is capable of joining a zone. 
 * @author timothyb89
 */
public class Entity implements TargetedSerializable<Entity>, EventBusProvider {

	/**
	 * A private list of zones that this entity is currently in. Note that this
	 * list only stays in sync with the actual zone addition status and thus
	 * adding a zone to this list should not cause 
	 */
	private List<Zone> zones;
	
	protected EventBus bus;
	
	public Entity() {
		zones = new LinkedList<>();
		
		bus = new EventBus() {{
			add(EntityZoneEnteredEvent.class);
			add(EntityZoneExitedEvent.class);
		}};
	}
	
	@Override
	public EventBusClient bus() {
		return bus.getClient();
	}
	
	/**
	 * Returns the first instance of a {@link Zone} of the given type that this
	 * entity is a member of. The zone list is maintained by 
	 * @param <T> the Zone subclass
	 * @param zone the class of the zone to retrieve
	 * @return the first matching zone, or {@code null} if none is found
	 */
	public <T extends Zone> T getZoneByType(Class<T> zone) {
		for (Zone z : zones) {
			if (z.getClass().equals(zone)) {
				return (T) z;
			}
		}
		
		return null;
	}
	
	/**
	 * An internal method intended to be called exclusively by {@link Zone} when
	 * this entity has been added to the zone. Specifically, this appends the
	 * given zone to the internal zone list, and fires a
	 * {@link EntityZoneEnteredException}.
	 * @param zone The zone entered
	 */
	public void _enteredZone(Zone zone) {
		zones.add(zone);
		
		bus.push(new EntityZoneEnteredEvent(this, zone));
	}
	
	/**
	 * An internal method intended to be called exclusively by {@link Zone} when
	 * this entity has been removed from the zone. Specifically, this removes
	 * the zone from the internal zone list, and fires a
	 * {@link EntityZoneExitedEvent}.
	 * @param zone 
	 */
	public void _exitedZone(Zone zone) {
		zones.add(zone);
		
		bus.push(new EntityZoneExitedEvent(this, zone));
	}
	
	protected void serializeInternal(JsonGenerator g) throws IOException {
		
	}
	
	@Override
	public void serializeFor(JsonGenerator g, Entity target) throws IOException {
		// in the future, this may limit the sending of certain internal
		// variables to the client that would, for example, be exposed only to
		// admins, or only to entities with a certain relationship status
		serialize(g);
	}

	@Override
	public void serialize(JsonGenerator g) throws IOException {
		g.writeStartObject();
		g.writeEndObject();
	}
	
}
