package net.letscode.game.server.message.request;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import net.letscode.game.server.ClientSession;
import net.letscode.game.server.message.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A request that asks the client for authentication, and handles the response.
 * @author timothyb89
 */
public class AuthenticationRequest extends Request<AuthenticationRequest> {

	private Logger logger = LoggerFactory.getLogger(AuthenticationRequest.class);
	
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
			default:
				getSession().send(Notification.error("Invalid auth type"));
				break;
		}
		
	}
	
	private void handleLogin(String username, String password) {
		
	}
	
	private void handleRegister(String username, String password, String email) {
		
	}
	
	@Override
	protected void serializeSubclass(JsonGenerator g) throws IOException {
		g.writeStartObject();
		
		// nothing to write at the moment
		
		g.writeEndObject();
	}
	
}
