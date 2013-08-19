package net.letscode.game.api.controller.player;

import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.zone.chat.ChatController;
import net.letscode.game.api.zone.chat.ChatZoneMessage;
import net.letscode.game.api.zone.chat.ChatZone;
import net.letscode.game.api.zone.chat.ChatZoneMessageEvent;
import net.letscode.game.event.EventHandler;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.incoming.ChatMessageEvent;
import net.letscode.game.server.message.outgoing.ChatMessage;

/**
 * Defines a basic player chat controller, which handles messages coming from
 * a client, and dispatches them to the {@link ChatZone} for the player entity.
 * <p>Note that the this controller is specific to the client; a new controller
 * would need to be created to be used with a different ClientSession.</p>
 * @author timothyb89
 */
@Slf4j
public class PlayerChatController implements ChatController {
	
	private ClientSession session;
	private Entity entity;
	private ChatZone zone;

	public PlayerChatController(ClientSession session) {
		this.session = session;
		
		session.getDispatcher().bus().register(this);
	}
	
	@Override
	public void onActivated(Entity entity) {
		this.entity = entity;
		
		// TODO: this is a pretty quick hack, and just picks the first ChatZone
		// (if any) that the player is a member of
		// in the future, this class will need to dispatch between multiple
		// zones properly, probably by using some zone UUID
		
		zone = entity.getZoneByType(ChatZone.class);
		
		// also register for chat zone events
		zone.bus().register(this);
	}
	
	/**
	 * Called when a chat message has been received from the player.
	 * @param event the chat 
	 */
	@EventHandler
	public void handleChatMessage(ChatMessageEvent event) {
		if (zone == null) {
			log.warn("Entity " + entity + " attempted to chat, but is not in a "
					+ "chat zone.");
			return;
		}
		
		log.info("Chat message being sent to zone: " + event);
		
		chat(zone, new ChatZoneMessage(
				entity,
				event.getText(),
				ChatZoneMessage.TYPE_NORMAL));
	}
	
	/**
	 * Handles a ChatZone message event; that is, an event that occurs when
	 * some other zone member sends a message to the zone (or possibly even this
	 * client).
	 * @param event the event
	 */
	@EventHandler
	public void handleChatZoneMessage(ChatZoneMessageEvent event) {
		log.info("Pushing message from zone to client: " + event);
		
		// just push the message directly to the client for now
		// later we may need to do some checking to make sure it actually should
		// be sent (too far away within a zone or something)
		
		session.send(new ChatMessage(event.getMessage()));
	}

	@Override
	public void chat(ChatZone zone, ChatZoneMessage message) {
		if (entity == null) {
			throw new IllegalStateException(
					"This controller has no associated entity");
		}
		
		// nothing too special here
		
		zone.chat(message);
	}

	@Override
	public void onDeactivated(Entity e) {
		// remove events
		zone.bus().deregister(e);
		
		this.entity = null;
		this.zone = null;
	}
	
}
