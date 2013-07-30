package net.letscode.game.server.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.letscode.game.api.entity.Entity;
import net.letscode.game.api.util.JsonSerializable;
import net.letscode.game.auth.User;
import net.letscode.game.event.EventBus;
import net.letscode.game.event.EventBusClient;
import net.letscode.game.event.EventBusProvider;
import net.letscode.game.server.message.MessageDispatcher;
import net.letscode.game.server.message.outgoing.StateChangeMessage;
import net.letscode.game.server.message.request.handler.RequestMonitor;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Defines a ClientSession. A {@code ClientSession} handles the base interaction
 * with each client session. Specifically, this entails processing incoming and
 * outgoing messages and ensuring that they are passed to the necessary event
 * processing classes. For specific details on how this occurs, see the
 * {@link RequestMonitor} and the {@link MessageDispatcher}.
 * @author timothyb89
 */
@Slf4j
@WebSocket
public class ClientSession extends WebSocketAdapter implements EventBusProvider {
	
	private JsonFactory factory;
	
	private EventBus bus;
	
	private RequestMonitor monitor;
	
	@Getter
	private MessageDispatcher dispatcher; // TODO: make this more global-er?
	
	/**
	 * The user object, set during the login process. 
	 */
	@Getter
	@Setter
	private User user;
	
	/**
	 * The player adapter, which handles registration of player Controller
	 * implementations.
	 */
	@Getter
	private PlayerAdapter adapter;
	
	/**
	 * The selected entity for this client session. Note that this can be any
	 * entity in particular - the actual character entities for the user are
	 * stored in the {@link #user} object which is set during the login process.
	 */
	@Getter
	private Entity entity;
	
	public ClientSession() {
		factory = new JsonFactory();
		
		bus = new EventBus() {{
			add(IncomingMessageEvent.class);
			add(OutgoingMessageEvent.class);
			add(EntitySelectionEvent.class);
			// Login event?
			// Logout event?
		}};
		
		monitor = new RequestMonitor();
		bus.register(monitor);
		
		dispatcher = new MessageDispatcher();
		bus.register(dispatcher);
		
		adapter = new PlayerAdapter(this);
		
		log.info("Initialized");
	}
	
	@Override
	public EventBusClient bus() {
		return bus.getClient();
	}
	
	//
	// networking / websocket functions
	//
	
	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		
		log.info("Client " + sess.getRemoteAddress() +  " connected");
		
		// TODO: get authentication working
		/*
		// immediately ask for authentication
		try {
			send(new AuthenticationRequest(this));
		} catch (Exception ex) {
			log.error("Failed to send authentication request", ex);
		}
		*/
		
		// for now, we'll just use a dummy entity and immediately put the player
		// into the game
		setEntity(new Entity());		
		send(new StateChangeMessage(StateChangeMessage.STATE_ZONE));
	}

	@Override
	public void onWebSocketText(String input) {
		super.onWebSocketText(input);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(input);
			
			log.info("Message: " + root);
			
			// notify the listeners
			bus.push(new IncomingMessageEvent(this, root));
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

			bus.push(new OutgoingMessageEvent(this, s));
		} catch (IOException ex) {
			log.error("Error sending message", ex);
		}
	}
	
	//
	// player session methods
	//
	
	/**
	 * Sets the entity that this client is associated with. This will fire an
	 * {@link EntitySelectionEvent}.
	 * @return the previous entity, if any
	 */
	public Entity setEntity(Entity entity) {
		Entity old = entity;
		
		this.entity = entity;
		
		bus.push(new EntitySelectionEvent(this, old, entity));
		
		return old;
	}
	
}
