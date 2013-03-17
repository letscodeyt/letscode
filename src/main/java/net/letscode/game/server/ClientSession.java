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
import org.eclipse.jetty.websocket.api.WebSocketConnection;
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
public class ClientSession {
	
	private Logger logger = LoggerFactory.getLogger(ClientSession.class);
	
	private JsonFactory factory;
	private WebSocketConnection socket;
	
	private List<SessionListener> listeners;
	
	public ClientSession() {
		factory = new JsonFactory();
		
		listeners = new LinkedList<SessionListener>();
		listeners.add(new RequestMonitor());
		listeners.add(new MessageDispatcher());
		
		logger.info("Initialized");
	}
	
	@OnWebSocketConnect
	public void onConnect(WebSocketConnection socket) {
		this.socket = socket;
		
		logger.info("Client " + socket.getRemoteAddress() +  " connected, "
				+ "protocol: " + socket.getSubProtocol());
		
		// immediately ask for authentication
		try {
			send(new AuthenticationRequest(this));
		} catch (Exception ex) {
			logger.error("Failed to send authentication request", ex);
		}
	}
	
	@OnWebSocketMessage
	public void onMessage(String input) {
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
	
	@OnWebSocketClose
	public void onClose(int status, String reason) {
		logger.info("Client " + socket.getRemoteAddress() + " closed connection, "
				+ "status: " + status + ", "
				+ "reason: " + reason);
	}

	public WebSocketConnection getSocket() {
		return socket;
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
		
		socket.write(writer.toString());
		
		for (SessionListener l : listeners) {
			l.onMessageSent(this, s);
		}
	}
	
}
