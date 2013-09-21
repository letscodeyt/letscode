package net.letscode.game.api.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.letscode.game.api.util.TargetedSerializable;
import net.letscode.game.api.zone.Zone;
import net.letscode.game.event.EventBus;
import net.letscode.game.event.EventBusClient;
import net.letscode.game.event.EventBusProvider;

/**
 * Defines a basic entity. An entity is essentially some server-side object,
 * unique only in that it is capable of joining a zone. Additionally, entities
 * have controllers that allow them to perform actions within the zone.
 * @author timothyb89
 */
public class Entity implements TargetedSerializable<Entity>, EventBusProvider {

	/**
	 * A private list of zones that this entity is currently in. Note that this
	 * list only stays in sync with the actual zone addition status and thus
	 * adding a zone to this list should not cause any actual zone join event.
	 */
	private List<Zone> zones;
	
	private Map<Class<? extends Controller>, Controller> controllers;
	
	protected EventBus bus;
	
	public Entity() {
		zones = new LinkedList<>();
		
		controllers = new HashMap<>();
		
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
	 * Gets the list of zones this entity is currently a member of. This list
	 * can not be modified; {@link Zone#addEntity(Entity)} should be used to
	 * join a zone instead.
	 * @return an unmodifiable list of zones this entity is a member of
	 */
	public List<Zone> getZones() {
		return Collections.unmodifiableList(zones);
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
	 * @param zone the zone that was exited
	 */
	public void _exitedZone(Zone zone) {
		zones.add(zone);
		
		bus.push(new EntityZoneExitedEvent(this, zone));
	}
	
	/**
	 * Gets the controller that provides an implementation for the given class.
	 * While there is no strict requirement as to the actual type of class
	 * provided, by convention it should be the highest-level interface that
	 * can provide the required functionality, and not a concrete class.
	 * @param <T> the controller type
	 * @param clazz the class for which to get a Controller instance
	 * @return a Controller implementation for the given class, or null
	 */
	public <T extends Controller> T getController(Class<T> clazz) {
		return (T) controllers.get(clazz);
	}
	
	/**
	 * Registers the given implementation of a controller for the given
	 * controller class. Note that the implementation must be a subclass of the
	 * specified controller class, otherwise an {@link IllegalArgumentException}
	 * will be thrown. Once set, the given controller is immediately activated
	 * (specifically, {@link Controller#onActivated(Entity)} is called) and it
	 * can begin functioning. Any existing controller will first be deactivated,
	 * and then returned.
	 * <p>Note that a {@code null} implementation may be provided. In this case,
	 * any existing controller is deactivated.</p>
	 * @param <T> the controller type to register
	 * @param clazz the class to provide an implementation for
	 * @param impl the implementation to active
	 * @return the previous Controller implementation, or {@code null} if none
	 *     exists.
	 * @throws IllegalArgumentException when an invalid controller type is
	 *     provided
	 */
	public <T extends Controller> T setController(
			Class<T> clazz, Controller impl) {
		// the controller class is checked at runtime, so it's safe to cast
		// later
		if (impl == null || !clazz.isAssignableFrom(impl.getClass())) {
			throw new IllegalArgumentException(
					"Attempted to register an incompatible controller for the "
					+ "controller class " + clazz);
		}
		
		Controller old = controllers.get(clazz);
		if (old != null) {
			old.onDeactivated(this);
		}
		
		controllers.put(clazz, impl);
		impl.onActivated(this);
		
		if (old == null) {
			return null;
		}
		
		// we know that this should be castable, assuming that it was originally
		// set using this method
		return (T) old;
	}
	
	/**
	 * Deactivates and removes all controllers currently associated with this
	 * Entity.
	 */
	public void purgeControllers() {
		for (Class<? extends Controller> c : controllers.keySet()) {
			setController(c, null);
		}
	}
	
	/**
	 * Retrieves an external representation of this entity for the given zone.
	 * This is intended to compliment the possibility of entities existing in
	 * multiple zones concurrently, allowing them to "appear" differently in
	 * each.
	 * 
	 * <p>Clients may decide how (and if) they would like to handle entities
	 * defining an external representation. For example, in a 2D zone, clients
	 * may decide to render the provided external entity view, but keep all
	 * other properties of the original entity. Note that this functionality is
	 * never guaranteed, and is not intended to provide any form of "security",
	 * as the original (this) entity is still fully exposed to clients.</p>
	 * 
	 * <p>By default, this simply returns {@code null}; as such, clients should
	 * be expected to display only the current entity.</p>
	 * 
	 * <p>TODO: this is currently unimplemented elsewhere, and is likely to be
	 * ignored completely.</p>
	 * @param zone the zone for which to retrieve an external representation
	 * @return an (optionally used) external representation of this entity
	 *     within the given zone, or null if none is defined.
	 */
	public Entity getRepresentationForZone(Zone zone) {
		return null;
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
