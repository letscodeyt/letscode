package net.letscode.game.api.zone.chat;

import lombok.Data;
import net.letscode.game.api.entity.Entity;

/**
 * Defines the basic properties for a ChatMessage.
 * @author timothyb89
 */
@Data
public class ChatZoneMessage {
	
	public static final String TYPE_ACTION = "action";
	public static final String TYPE_NORMAL = "shout";
	
	private final Entity sender;
	private final String text;
	private final String type;
	
}
