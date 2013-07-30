package net.letscode.game.server;

import net.letscode.game.server.client.ClientSession;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author timothyb89
 */
public class ClientHandlerServlet extends WebSocketServlet {
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(ClientSession.class);
		
		// this doesn't seem to work yet (jetty 9M5)
		factory.getPolicy().setIdleTimeout(TimeUnit.MINUTES.toMillis(5));
	}
	
}
