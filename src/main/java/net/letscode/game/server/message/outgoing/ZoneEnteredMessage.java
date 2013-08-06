package net.letscode.game.server.message.outgoing;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.letscode.game.api.zone.Zone;

/**
 * A message that notifies a client that they have joined a zone. This message
 * should provide the initial zone sync, and will likely be the only 'full copy'
 * of the zone sent to clients (save for a possible resync)
 * @author timothyb89
 */
public class ZoneEnteredMessage extends OutgoingMessage {

	private Zone zone;

	public ZoneEnteredMessage(Zone zone) {
		super("zone-entered");
		
		this.zone = zone;
	}
	
	@Override
	protected void serializeSubclass(JsonGenerator g) throws IOException {
		
	}
	
}
