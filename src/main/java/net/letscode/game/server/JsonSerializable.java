package net.letscode.game.server;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;

/**
 *
 * @author tim
 */
public interface JsonSerializable {
	
	public void serialize(JsonGenerator g) throws IOException;
	
}
