package net.letscode.game.api.controller.player;

import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.zone.chat.ChatController;
import net.letscode.game.api.zone.chat.ChatMessage;
import net.letscode.game.api.zone.chat.ChatZone;
import net.letscode.game.event.EventHandler;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.incoming.ChatMessageEvent;

/**
 * Defines a basic player chat controller, which handles messages coming from
 * a client, and dispatches them to the {@link ChatZone} for the player entity.
 * <p>Note that the this controller is specific to the client; a new controller
 * 
 * @author timothyb89
 */
@Slf4j
public class PlayerChatController implements ChatController {
	
	private Entity entity;

	public PlayerChatController(ClientSession session) {
		session.getDispatcher().bus().register(this);
	}
	
	@Override
	public void onActivated(Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Called when a chat message has been received from the player.
	 * @param event the chat 
	 */
	@EventHandler
	public void handleChatMessage(ChatMessageEvent event) {
		ChatZone zone = entity.getZoneByType(ChatZone.class);
		if (zone == null) {
			log.warn("Entity " + entity + " attempted to chat, but is not in a "
					+ "chat zone.");
			return;
		}
		
		log.info("Chat message being sent to zone: " + event);
		
		chat(zone, new ChatMessage(
				entity,
				event.getText(),
				ChatMessage.TYPE_NORMAL));
	}

	@Override
	public void chat(ChatZone zone, ChatMessage message) {
		if (entity == null) {
			throw new IllegalStateException(
					"This controller has no associated entity");
		}
		
		// nothing too special here
		
		zone.chat(message);
	}

	@Override
	public void onDeactivated(Entity e) {
		this.entity = null;
	}
	
}
