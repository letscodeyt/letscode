package net.letscode.game.server.message.outgoing;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import lombok.Getter;
import net.letscode.game.api.util.JsonSerializable;

/**
 * Defines a basic outgoing message
 * @author timothyb89
 */
public abstract class OutgoingMessage implements JsonSerializable {

	@Getter
	private final String type;

	public OutgoingMessage(String type) {
		this.type = type;
	}
	
	/**
	 * Writes subclass data for this message. Subclasses are expected to leave
	 * the JSON generator in the same state it was in when the method was
	 * called. Specifically, this means that any new elements should be closed
	 * and the generator should be at the same 'level' of the JSON tree.
	 * @param g the generator to output to
	 * @throws IOException 
	 */
	protected abstract void serializeSubclass(JsonGenerator g) throws IOException;

	@Override
	public void serialize(JsonGenerator g) throws IOException {
		g.writeStartObject();
		g.writeStringField("type", type);
		
		g.writeEndObject();
	}
	
}
