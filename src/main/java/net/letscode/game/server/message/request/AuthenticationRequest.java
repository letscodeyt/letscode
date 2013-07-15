package net.letscode.game.server.message.request;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.server.ClientSession;
import net.letscode.game.server.message.Notification;

/**
 * A request that asks the client for authentication, and handles the response.
 * @author timothyb89
 */
@Slf4j
public class AuthenticationRequest extends Request<AuthenticationRequest> {
	
	public AuthenticationRequest(ClientSession session) {
		super(session);
	}
	
	@Override
	public String getName() {
		return "authenticate";
	}

	@Override
	public void onResponseReceived(JsonNode root) {
		// look for either a login or register message
		if (!root.has("data")) {
			getSession().send(Notification.error("Malformed message"));
			return;
		}
		
		JsonNode data = root.get("data");
		
		switch (data.path("type").asText()) {
			case "login":
				handleLogin(
						data.path("username").asText(),
						data.path("password").asText());
				break;
			case "register":
				handleRegister(
						data.path("username").asText(),
						data.path("email").asText(),
						data.path("password").asText());
				break;
			default:
				getSession().send(Notification.error("Invalid auth type"));
				break;
		}
		
	}
	
	private void handleLogin(String username, String password) {
		log.info("Attempting to login " + username);
		getSession().send(Notification.error("omg wat"));
	}
	
	private void handleRegister(String username, String password, String email) {
		log.info("Attempting to register " + username);
	}
	
	@Override
	protected void serializeSubclass(JsonGenerator g) throws IOException {
		g.writeStartObject();
		
		// nothing to write at the moment
		
		g.writeEndObject();
	}
	
}
