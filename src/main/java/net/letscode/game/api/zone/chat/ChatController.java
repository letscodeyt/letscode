package net.letscode.game.api.zone.chat;

import net.letscode.game.api.controller.Controller;

/**
 * Provides a simple chat controller than can be used by any entity.
 * @author timothyb89
 */
public interface ChatController extends Controller {

	public void chat(ChatZone zone, ChatZoneMessage message);
	
}
