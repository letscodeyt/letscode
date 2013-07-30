package net.letscode.game.server.message.outgoing;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import lombok.Getter;

/**
 * Sends a state change message to the client. A state change message indicates
 * that the client should begin to expect messages of the specified type.
 * Usually, this means that the client should transition from, for example, the
 * login screen to the zone view.
 * @author timothyb89
 */
public class StateChangeMessage extends OutgoingMessage {

	public static final String STATE_CONNECT = "connect";
	public static final String STATE_LOGIN = "login";
	public static final String STATE_ZONE = "zone";

	@Getter
	private final String state;
	
	public StateChangeMessage(String state) {
		super("state-change");
		
		this.state = state;
	}
	
	@Override
	protected void serializeSubclass(JsonGenerator g) throws IOException {
		g.writeStringField("state", state);
	}
	
}
