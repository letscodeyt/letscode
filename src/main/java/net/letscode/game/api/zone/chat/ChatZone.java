package net.letscode.game.api.zone.chat;

import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.zone.Zone;

/**
 * A simple zone that implements basic chatroom functionality. Chat zones are
 * @author timothyb89
 */
public class ChatZone extends Zone {

	public ChatZone() {
		bus.add(ChatZoneExitedEvent.class);
		bus.add(ChatZoneMessageEvent.class);
	}

	/**
	 * Removes the entity from this zone, triggering a
	 * {@link ChatZoneExitedEvent} with the given {@code message}.
	 * @see Zone#removeEntity(Entity) 
	 * @param entity the entity to remove from the zone
	 * @param message the zone parting message
	 */
	public void removeEntity(Entity entity, String message) {
		entities.remove(entity);
		entity._exitedZone(this);
		
		fireZoneExitedEvent(entity, message);
	}

	/**
	 * Overrides {@link Zone#fireZoneExitedEvent(Entity)} to push a
	 * {@link ChatZoneExitedEvent} to the event bus. Note that listeners for
	 * {@link ZoneExitedEvent} will still be notified, but will need to cast the
	 * event.
	 * <p>Unlike {@link #fireZoneExitedEvent(Entity, String)}, no exit message
	 * is provided; as such, the {@code message} field will simply be left null
	 * in the resulting {@code ChatZoneExitedEvent}.
	 * @see Zone#removeEntity(Entity)
	 * @param entity the entity that exited the zone
	 */
	@Override
	protected void fireZoneExitedEvent(Entity entity) {
		bus.push(new ChatZoneExitedEvent(this, entity));
	}
	
	/**
	 * This fires a {@link ChatZoneExitedEvent} containing an optional exit
	 * message. Entities may be removed from the zone with a message by calling
	 * {@link #removeEntity(Entity, String)}. The message may be null, as is the
	 * case when the ordinary {@link removeEntity(Entity)} is called.
	 * @see #removeEntity(Entity, String) 
	 * @param entity the entity that left the zone
	 * @param message the zone exit message, if any
	 */
	protected void fireZoneExitedEvent(Entity entity, String message) {
		bus.push(new ChatZoneExitedEvent(this, entity, message));
	}
	
	/**
	 * @param message the message to push to the zone
	 */
	public void chat(ChatMessage message) {
		bus.push(new ChatZoneMessageEvent(this, message));
	}
	
	/**
	 * Gets the active chat controller, if any, for the given entity. This is a
	 * short wrapper for {@link Entity#getController(Class)}.
	 * @see Entity#getController(Class) 
	 * @param e the entity for which to get a ChatController
	 * @return the {@code ChatController} for the given entity, or {@code null}
	 */
	public ChatController getChatController(Entity e) {
		return e.getController(ChatController.class);
	}
	
}
