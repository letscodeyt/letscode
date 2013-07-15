package net.letscode.game.server.message;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.letscode.game.api.util.JsonSerializable;

/**
 * A simple message type for notification. 
 * @author tim
 */
public class Notification implements JsonSerializable {
	
	public static final String JSON_TYPE = "notification";
	
	public static final String CLASS_INFO = "info";
	public static final String CLASS_WARNING = "warning";
	public static final String CLASS_ERROR = "error";
	
	private String clazz;
	private String message;

	public Notification() {
	}

	public Notification(String clazz, String message) {
		this.clazz = clazz;
		this.message = message;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public void serialize(JsonGenerator g) throws IOException {
		g.writeStartObject();
		
		g.writeStringField("type", JSON_TYPE);
		g.writeStringField("class", clazz);
		g.writeStringField("message", message);
		
		g.writeEndObject();
	}
	
	public static Notification info(String text) {
		return new Notification(CLASS_INFO, text);
	}
	
	public static Notification warning(String text) {
		return new Notification(CLASS_WARNING, text);
	}
	
	public static Notification error(String text) {
		return new Notification(CLASS_ERROR, text);
	}
	
}
