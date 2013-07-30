package net.letscode.game.server.client;

import lombok.Data;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.event.Event;

/**
 * An event that occurs after a player has connected and selected an entity.
 * @author timothyb89
 */
@Data
public class EntitySelectionEvent extends Event {
	
	private final ClientSession session;
	private final Entity previous;
	private final Entity entity;
	
}
