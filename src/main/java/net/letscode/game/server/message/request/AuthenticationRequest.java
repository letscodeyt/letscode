package net.letscode.game.server.message.request;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import net.letscode.game.server.ClientSession;
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
		// look for either a register 
		logger.info("Response received: " + root.toString());
	}
	
	@Override
	protected void serializeSubclass(JsonGenerator g) throws IOException {
		g.writeStartObject();
		
		// nothing to write at the moment
		
		g.writeEndObject();
	}
	
}
