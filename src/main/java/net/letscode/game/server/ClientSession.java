package net.letscode.game.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import net.letscode.game.server.message.MessageDispatcher;
import net.letscode.game.server.message.request.AuthenticationRequest;
import net.letscode.game.server.message.request.Request;
import net.letscode.game.server.message.request.handler.RequestMonitor;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author timothyb89
 */
@WebSocket
public class ClientSession extends WebSocketAdapter {
	
	private Logger logger = LoggerFactory.getLogger(ClientSession.class);
	
	private JsonFactory factory;
	
	private List<SessionListener> listeners;
	
	public ClientSession() {
		factory = new JsonFactory();
		
		listeners = new LinkedList<SessionListener>();
		listeners.add(new RequestMonitor());
		listeners.add(new MessageDispatcher());
		
		logger.info("Initialized");
	}

	@Override
	public void onWebSocketConnect(Session sess) {
		logger.info("Client " + sess.getRemoteAddress() +  " connected");
		
		// immediately ask for authentication
		try {
			send(new AuthenticationRequest(this));
		} catch (Exception ex) {
			logger.error("Failed to send authentication request", ex);
		}
	}

	@Override
	public void onWebSocketText(String input) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(input);
			
			// notify the listeners
			for (SessionListener l : listeners) {
				l.onMessageReceived(this, root);
			}
			
			logger.info("Message: " + root);
		} catch (Exception ex) {
			logger.error("Error parsing client message", ex);
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		logger.info("Client " + socket.getRemoteAddress() + " closed connection, "
				+ "status: " + status + ", "
				+ "reason: " + reason);
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
	public void send(JsonSerializable s) throws IOException {
		// TODO: use a WebSocketWriter here when it's been implemented by
		// Jetty so we don't have to keep the whole message in memory.
		
		StringWriter writer = new StringWriter();
		JsonGenerator g = factory.createGenerator(writer);
		s.serialize(g);
		g.close();
		
		// TODO: Fixme
		getSession().s(writer.toString());
		
		for (SessionListener l : listeners) {
			l.onMessageSent(this, s);
		}
	}
	
}
