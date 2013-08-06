package net.letscode.game.server.message.incoming;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;
import net.letscode.game.server.client.ClientSession;
import net.letscode.game.server.message.MessageHandler;

/**
 * Defines a registration message.
 * @author timothyb89
 */
@ToString(exclude = {"password"})
@MessageHandler("register")
public class RegistrationMessageEvent extends AbstractMessageEvent {
	
	@Getter
	private final String username;
	
	@Getter
	private final String password;
	
	@Getter
	private final String email;
	
	public RegistrationMessageEvent(ClientSession session, JsonNode node) {
		super(session, node);
		
		username = validateTextField(node.get("username"));
		password = validateTextField(node.get("password"));
		email = validateTextField(node.get("email"));
	}
	
}
