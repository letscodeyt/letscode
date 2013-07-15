package net.letscode.game.api.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.letscode.game.api.util.TargetedSerializable;

/**
 *
 * @author timothyb89
 */
public class Entity implements TargetedSerializable<Entity> {

	protected void serializeInternal(JsonGenerator g) throws IOException {
		
	}
	
	@Override
	public void serializeFor(JsonGenerator g, Entity target) throws IOException {
		// in the future, this may limit the sending of certain internal
		// variables to the client that would, for example, be exposed only to
		// admins, or only to entities with a certain relationship status
		serialize(g);
	}

	@Override
	public void serialize(JsonGenerator g) throws IOException {
		g.writeStartObject();
		g.writeEndObject();
	}
	
	
	
}
