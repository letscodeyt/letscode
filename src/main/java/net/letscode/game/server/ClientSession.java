package net.letscode.game.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.util.JsonSerializable;
import net.letscode.game.server.message.MessageDispatcher;
import net.letscode.game.server.message.request.AuthenticationRequest;
import net.letscode.game.server.message.request.handler.RequestMonitor;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * 
 * @author timothyb89
 */
@Slf4j
@WebSocket
public class ClientSession extends WebSocketAdapter {
	
	private JsonFactory factory;
	
	private List<SessionListener> listeners;
	
	public ClientSession() {
		factory = new JsonFactory();
		
		listeners = new LinkedList<>();
		listeners.add(new RequestMonitor());
		listeners.add(new MessageDispatcher());
		
		log.info("Initialized");
	}
	
	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		
		log.info("Client " + sess.getRemoteAddress() +  " connected");
		
		// immediately ask for authentication
		try {
			send(new AuthenticationRequest(this));
		} catch (Exception ex) {
			log.error("Failed to send authentication request", ex);
		}
	}

	@Override
	public void onWebSocketText(String input) {
		super.onWebSocketText(input);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(input);
			
			log.info("Message: " + root);
			
			// notify the listeners
			for (SessionListener l : listeners) {
				l.onMessageReceived(this, root);
			}
		} catch (Exception ex) {
			log.error("Error parsing client message", ex);
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		
		log.info("Client " + getSession().getRemoteAddress() + " "
				+ "closed connection, "
				+ "status: " + statusCode + ", "
				+ "reason: " + reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		
		log.error(
				"WebSocket error in " + getSession().getRemoteAddress(), cause);
	}
	
	public void addListener(SessionListener l) {
		listeners.add(l);
	}
	
	public void removeListener(SessionListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Sends a {@code JsonSerializable} to the client.
	 * @param s the object to send
	 * @throws IOException 
	 */
	public void send(JsonSerializable s) {
		// TODO: use a WebSocketWriter here when it's been implemented by
		// Jetty so we don't have to keep the whole message in memory.
		
		try {
			// this throws an IOException but should never happen as no real
			// IO occurs
			StringWriter writer = new StringWriter();
			JsonGenerator g = factory.createGenerator(writer);
			s.serialize(g);
			g.close();

			// TODO: Fixme
			getRemote().sendStringByFuture(writer.toString());

			for (SessionListener l : listeners) {
				l.onMessageSent(this, s);
			}
		} catch (IOException ex) {
			log.error("Error sending message", ex);
		}
	}
	
}
