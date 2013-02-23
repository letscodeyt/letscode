package net.letscode.game.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import net.letscode.game.server.message.request.AuthenticationRequest;
import net.letscode.game.server.message.request.Request;
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
	
	private Map<String, Request> requests;
	
	public ClientSession() {
		factory = new JsonFactory();
		requests = new HashMap<String, Request>();
		logger.info("Initialized");
	}
	
	@OnWebSocketConnect
	public void onConnect(WebSocketConnection socket) {
		this.socket = socket;
		
		logger.info("Client " + socket.getRemoteAddress() +  " connected, "
				+ "protocol: " + socket.getSubProtocol());
		
		// immediately ask for authentication
		try {
			sendRequest(new AuthenticationRequest(this));
		} catch (Exception ex) {
			logger.error("Failed to send authentication request", ex);
		}
	}
	
	@OnWebSocketMessage
	public void onMessage(String input) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(input);
			
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
	
	/**
	 * Sends a {@code JsonSerializable} to the client. Note that this method
	 * should not be used to send instances of {@link Request} directly as they
	 * will not be notified. If you expect a response, you should use
	 * {@link sendRequest(Request)} instead.
	 * @param s the object to send
	 * @throws IOException 
	 */
	public void send(JsonSerializable s) throws IOException {
		StringWriter writer = new StringWriter();
		JsonGenerator g = factory.createGenerator(writer);
		s.serialize(g);
		g.close();
		
		socket.write(writer.toString());
	}
	
	/**
	 * Sends a request to this client. Requests differ from usage of
	 * {@link send(JsonSerializable)} in that they expect a response from the
	 * client, and will automatically be notified if and when one is received.
	 * <p>Responses will be paired with the originating request by their
	 * {@code id}, a randomly generated 64-bit hex string. When a
	 * {@code response} with a matching id is sent from the client, the request
	 * will be notified.</p>
	 * <p>Note that while {@link send(JsonSerializable)} will actually send
	 * the request object to the client, no notification will be dispatched if
	 * the client responds. </p>
	 * @param r the request to send
	 */
	public void sendRequest(Request r) throws IOException {
		requests.put(r.getId(), r);
		send(r);
	}
	
}
