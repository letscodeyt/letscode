package net.letscode.game.server.message.request;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.letscode.game.server.ClientSession;
import net.letscode.game.server.JsonSerializable;

/**
 * Represents a request that can be sent to the client. 
 * @author timothyb89
 */
public abstract class Request<S extends Request> implements JsonSerializable {
	
	public static final String JSON_TYPE = "request";
	
	private ClientSession session;
	private String id;
	private List<Listener<S>> listeners;
	
	public Request(ClientSession session) {
		this.session = session;
		
		id = Long.toHexString(UUID.randomUUID().getLeastSignificantBits());
		listeners = new ArrayList<Listener<S>>();
	}

	public ClientSession getSession() {
		return session;
	}

	public String getId() {
		return id;
	}

	public abstract String getName();
	
	public List<Listener<S>> getListeners() {
		return listeners;
	}
	
	public void addListener(Listener<S> l) {
		listeners.add(l);
	}

	/**
	 * A chainable method used to add one or many listeners to this
	 * {@code Request}.
	 * @param l the listener to add
	 * @return this {@code Request}
	 */
	public S listener(Listener<S> l) {
		listeners.add(l);
		
		return (S) this;
	}
	
	public void notify(JsonNode root) {
		onResponseReceived(root);
		for (Listener l : listeners) {
			l.onResponseReceived(this, root);
		}
	}
	
	/**
	 * A response event stub that subclasses can implement directly, without
	 * needing to create a Listener.
	 * @param root the root node of the JSON-formatted response
	 */
	public void onResponseReceived(JsonNode root) {
		
	}
	
	/**
	 * Serializes this {@code Request} to JSON. This is generally used to
	 * prepare data to be sent to a client.
	 * @param g the {@link JsonGenerator} to use
	 * @throws IOException on a surprisingly unlikely IO error
	 */
	@Override
	public void serialize(JsonGenerator g) throws IOException {
		g.writeStartObject();
		
		g.writeStringField("type", JSON_TYPE);
		g.writeStringField("name", getName());
		g.writeStringField("id", getId());
		
		g.writeFieldName("data");
		serializeSubclass(g);
		
		g.writeEndObject();
	}
	
	protected abstract void serializeSubclass(JsonGenerator g) throws IOException;
	
	public interface Listener<T extends Request> {
		
		public void onResponseReceived(T r, JsonNode node);
		
	}
	
}
