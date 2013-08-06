package net.letscode.game.server.message.incoming;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MessageHandler;

/**
 * Defines a login message event. This event will automatically be instantiated
 * when a {@code login}-typed message is received. The message will then be
 * parsed and pushed to the event bus.
 * @author timothyb89
 */
@ToString(exclude = {"password"})
@MessageHandler("login")
public class LoginMessageEvent extends AbstractMessageEvent {

	@Getter
	private final String username;
	
	@Getter
	private final String password;
	
	public LoginMessageEvent(ClientSession session, JsonNode node) {
		super(session, node);
		
		username = validateTextField(node.get("username"));
		password = validateTextField(node.get("password"));
	}
	
}
