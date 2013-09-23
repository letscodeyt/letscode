package net.letscode.game.server.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.letscode.game.api.controller.player.PlayerChatController;
import net.letscode.game.api.controller.Controller;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.zone.chat.ChatController;

/**
 * An adapter class that manages the controllers for the player entity.
 * @author timothyb89
 */
public class PlayerAdapter {
	
	private Map<Class, Controller> controllers;
	private ClientSession session;
	
	public PlayerAdapter(ClientSession session) {
		this.session = session;
		
		controllers = new HashMap<>();
		
		initDefaultControllers();
	}
	
	private void initDefaultControllers() {
		set(ChatController.class, new PlayerChatController(session));
	}
	
	/**
	 * Sets the given controller instance as the default for this player 
	 * adapter. Note that this does not automatically update the controllers on
	 * the player entity, {@link #putControllers(Entity)} should be used to
	 * handle this.
	 * @param c the class to provide an implementation for
	 * @param controller the concrete implementation
	 */
	public void set(Class c, Controller controller) {
		controllers.put(c, controller);
	}
	
	/**
	 * Adds all of the registered player controllers to the given entity.
	 * Previous controller instances are returned in a map.
	 * @param entity the entity 
	 * @return a map of the previous controllers, if any
	 */
	public Map<Class, Controller> putControllers(Entity entity) {
		Map<Class, Controller> ret = new HashMap<>();
		
		for (Entry<Class, Controller> c : controllers.entrySet()) {
			Controller old = entity.setController(c.getKey(), c.getValue());
			controllers.put(c.getKey(), old);
		}
		
		return ret;
	}
	
}
